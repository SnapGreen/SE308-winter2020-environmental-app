#!/bin/bash
INFILE=$(grep -oP '(?<=INFILE:).*' settings.txt)
MAPFILE=$(grep -oP '(?<=MAPFILE:).*' settings.txt)
PRODS_PER_JSON=$(grep -oP '(?<=PRODS_PER_JSON:).*' settings.txt)
PATTERNFILE=$(grep -oP '(?<=PATTERNFILE:).*' settings.txt)
SPLIT_PREFIX=$(grep -oP '(?<=SPLIT_PREFIX:).*' settings.txt)
SUFFIX_LEN=$(grep -oP '(?<=SUFFIX_LEN:).*' settings.txt)
OUTFILE_END=$(grep -oP '(?<=OUTFILE_END:).*' settings.txt)
TMPFILE_END=$(grep -oP '(?<=TMPFILE_END:).*' settings.txt)
PREPPED_TMP="prepped.tmp"
INGREDIENTS_TMP="ingredients.tmp"
INGREDIENTSB4_TMP="ingredtientsb4.tmp"
NON_INGREDIENTS_TMP="non_ingredients.tmp"
SET_INGREDIENTS_TMP="set_ingredients.tmp"
RELEVANTDATA_TMP="relevantdata.tmp"
JOINED_TMP="joined.tmp"
SORTED_TMP="sorted.tmp"
AWK_FORMAT="csvtojson.awk"
AWK_SHRINK="consolidateIngreds.awk"
AWK_TRIM="trim.awk"
AWK_RM_INGRDS="removeIngreds.awk"
AWK_MAP="map_fdcid-gtin.awk"


function prepData(){
   printf "prepping data...\n"
   # makes sure any Windows <CR> are converted to linux \r
   dos2unix $1

   printf "removing malformed lines...\n"
   # adapted from Socowi's answer:
   # https://stackoverflow.com/questions/57167920/sed-awk-remove-newline-with-condition
   perl -np0 -e 's/\n(?!")//g' $1

   printf "removing category line...\n"
   # removes the first line of the input file and saves to a temp file
   # https://stackoverflow.com/questions/339483/how-can-i-remove-the-first-line-of-a-text-file-using-bash-sed-script
   tail -n +2 $1 > $2
}

function extractRelevantCols(){
   printf "extracting relevant columns...\n"
   > $3
   # created a reduced file consisting only of the parts we need
   awk -f $1 $2 >> $3
}

function createMap(){
   printf "creating fdcid-gtin map...\n"
   # clear the map file
   > $3
   # creates a map of fdc-id : gtin14 pairs
   # we need this to track FDA updates and modify them in the db
   # alternately, we could store it in the db
   awk -f $1 $2 >> $3
}

function isolateIngredients(){
   printf "splitting ingredients/non-ingredients...\n"
   # clear the temp ingreds file if it exists
   # create a separate file with ingredients only for sed to work on quickly
   > $3
   > $4
   awk -f $1 $2 >> $3 
   cut -d "|" -f  3 $2 >> $4
}

function cleanIngredients(){
   # make a copy for comparison
   cat $2 > $INGREDIENTSB4_TMP

   printf "cleaning ingredients (this takes a bit)...\n"
   # apply removal patterns to ingredients
   while read -r pattern;
   do
      sed -i "$pattern" $2
   done < $1
}

function consolidateIngredients(){
   printf "consolidating ingredients/non-ingredients...\n"
   # remove ingredients mentioned more than once
   > $3
   awk -f $1 $2 >> $3
}

function rejoinFiles(){
   printf "rejoining files...\n"
   # add ingredients back to other data
   # final format should be gtin|modifiedDate|ingreds
   > $3
   paste -d "|" $1 $2 >> $3
} 

function sortProducts(){
   printf "sorting products...\n"
   # sorts the file from lowest gtin to highest
   # this will help to keep things orderly in case something goes wrong
   >$2
   sort -t ',' -nk1 $1 >> $2
}

function removeBad(){
   # The FDA has two bad entries that have this number as their gtin
   # Any others are created by the script
   # This notifies us if any beyond the two are found
   # In any case, all are removed

   numbad=$(grep -o '^00000000000000' $1 | wc -l)

   if [[ $numbad -gt 2 ]] ; then 
      removenum=$((numbad-2))
      printf "removing $removenum entries...\n"
   else
      printf "no bad entries found...\n"
   fi

   sed -i '/^00000000000000/d' $1
}

function splitChunks(){
   printf "splitting int %d line chunks...\n" $PRODS_PER_JSON
   # splits the trimmed file
   # -l flag denotes we want to split files by $PRODS_PER_JSON number of lines
   # -d means we want digit suffixes
   # -a $SUFFIX_LEN means we want $SUFFIX_LEN digits in each suffix
   # -e means don't output zero size files
   # $SPLIT_PREFIX is the first part of what each file will be named
   # --additional-suffix will be added to the end
   split -l $PRODS_PER_JSON -d  -e $1 $SPLIT_PREFIX \
      --additional-suffix=$TMPFILE_END -a $SUFFIX_LEN
}

function createJsons(){
   printf "turning chunks into JSON files (longest part)...\n"
   for file in branded_food_[0-9]*.tmp
   do
      # extracting the suffix number from each temp file
      outnum=$(echo $file | egrep -o [0-9]+)
      # putting together an output file name
      outname="../${SPLIT_PREFIX}${outnum}${OUTFILE_END}"
      # make sure the outfile is clear, if it exists
      > $outname
      # place the first line into the outfile
      echo "{" >> $outname
      printf "\"products\": [\n" >> $outname
      # run awk to process the rest
      awk -f $1 $file >> $outname
      # removes comma from last line of last entry
      sed -i '$s/,$//' $outname
      #adds closing bracket to outfile
      printf "\t]\n" >> $outname
      echo "}" >> $outname
   done
}


prepData $INFILE $PREPPED_TMP

extractRelevantCols $AWK_TRIM $PREPPED_TMP $RELEVANTDATA_TMP

createMap $AWK_MAP $RELEVANTDATA_TMP $MAPFILE 

isolateIngredients $AWK_RM_INGRDS $RELEVANTDATA_TMP $NON_INGREDIENTS_TMP $INGREDIENTS_TMP 

cleanIngredients $PATTERNFILE $INGREDIENTS_TMP 

consolidateIngredients $AWK_SHRINK $INGREDIENTS_TMP $SET_INGREDIENTS_TMP 

rejoinFiles $NON_INGREDIENTS_TMP $SET_INGREDIENTS_TMP $JOINED_TMP

sortProducts $JOINED_TMP $SORTED_TMP

removeBad $SORTED_TMP

splitChunks $SORTED_TMP

createJsons $AWK_FORMAT

# removes all of the temporary files
# comment this out for testing & debugging
#rm *$TMPFILE_END

printf "...all done!\n"


