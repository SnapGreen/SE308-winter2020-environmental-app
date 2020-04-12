#!/bin/bash
INFILE="../branded_food.csv"
MAPFILE="../fdcid-gtin.txt"
TMPFILE="branded_food.tmp"
ASCIIFILE="branded_food_ascii.tmp"
TMPINGRDSFILE="ingredients.tmp"
TMPOTHERFILE="non-ingreds.tmp"
SINGLEINGRDSFILE="shrunkingreds.tmp"
PATTERNFILE="removalpatterns.txt"
TRIMFILE="branded_food_trimmed.tmp"
PREPFILE="branded_food_joined.tmp"
SORTEDFILE="branded_food_sorted.tmp"
SPLIT_PREFIX="branded_food_"
OUT_TMP_SUFFIX=".tmp"
SUFFIX_LEN="4"
OUT_SUFFIX=".json"
SPLIT_FILE_LINES="500"
AWK_FORMAT="csvtojson.awk"
AWK_SHRINK="consolidateIngreds.awk"
AWK_TRIM="trim.awk"
AWK_RM_INGRDS="removeIngreds.awk"
AWK_MAP="map_fdcid-gtin.awk"


function prepData(){
   printf "prepping data...\n"
   # makes sure any Windows <CR> are converted to linux \r
   dos2unix $1

   # converts to ascii char set
   # this seems to create more problems than it fixes
   #printf "converting to ascii...\n"
   #iconv -c -f utf-8 -t ascii $1 > $2

   # removes the first line of the input file and saves to a temp file
   # https://stackoverflow.com/questions/339483/how-can-i-remove-the-first-line-of-a-text-file-using-bash-sed-script
   printf "removing category line...\n"
   tail -n +2 $2 > $3
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
   > $1
   # creates a map of fdc-id : gtin14 pairs
   # we need this to track FDA updates and modify them in the db
   # alternately, we could store it in the db
   awk -f $2 $3 >> $1
}

function isolateIngredients(){
   printf "splitting ingredients/non-ingredients...\n"
   # clear the temp ingreds file if it exists
   # create a separate file with ingredients only for sed to work on quickly
   > $1
   > $2
   cut -d "|" -f  3 $4 >> $1
   awk -f $3 $4 >> $2 
}

function cleanIngredients(){
   printf "cleaning ingredients (this takes a bit)...\n"
   # apply removal patterns to ingredients
   while read -r pattern;
   do
      sed -i "$pattern" $1
   done < $2
}

function consolidateIngredients(){
   printf "consolidating ingredients/non-ingredients...\n"
   # remove ingredients mentioned more than once
   > $1
   awk -f $2 $3 >> $1
}

function rejoinFiles(){
   printf "rejoining files...\n"
   # add ingredients back to other data
   # final format should be gtin|modifiedDate|ingreds
   > $1
   paste -d "|" $2 $3 >> $1
} 

function sortProducts(){
   printf "sorting products...\n"
   # sorts the file from lowest gtin to highest
   # this will help to keep things orderly in case something goes wrong
   >$1
   sort -t ',' -nk1 $2 >> $1
}

function removeBad(){
   printf "removing bad entries...\n"
   sed -i '/^00000000000000/d' $1
}

function splitChunks(){
   printf "splitting int %d line chunks...\n" $SPLIT_FILE_LINES
   # splits the trimmed file
   # -l flag denotes we want to split files by $SPLIT_FILE_LINES number of lines
   # -d means we want digit suffixes
   # -a $SUFFIX_LEN means we want $SUFFIX_LEN digits in each suffix
   # -e means don't output zero size files
   # $SPLIT_PREFIX is the first part of what each file will be named
   # --additional-suffix will be added to the end
   split -l $SPLIT_FILE_LINES -d  -e $1 $SPLIT_PREFIX \
      --additional-suffix=$OUT_TMP_SUFFIX -a $SUFFIX_LEN
}

function createJsons(){
   printf "turning chunks into JSON files (longest part)...\n"
   for file in branded_food_[0-9]*.tmp
   do
      # extracting the suffix number from each temp file
      outnum=$(echo $file | egrep -o [0-9]+)
      # putting together an output file name
      outname="../${SPLIT_PREFIX}${outnum}${OUT_SUFFIX}"
      # make sure the outfile is clear, if it exists
      > $outname
      # place the first line into the outfile
      echo "\"products\": [" >> $outname
      # run awk to process the rest
      awk -f $AWK_FORMAT $file >> $outname
      # removes comma from last line of last entry
      sed -i '$s/,$//' $outname
      #adds closing bracket to outfile
      echo "]" >> $outname
   done
}



prepData $INFILE $ASCIIFILE $TMPFILE

extractRelevantCols $AWK_TRIM $TMPFILE $TRIMFILE

createMap $MAPFILE $AWK_MAP $TRIMFILE

isolateIngredients $TMPINGRDSFILE $TMPOTHERFILE $AWK_RM_INGRDS $TRIMFILE

cleanIngredients $TMPINGRDSFILE $PATTERNFILE

consolidateIngredients $SINGLEINGRDSFILE $AWK_SHRINK $TMPINGRDSFILE

rejoinFiles $PREPFILE $TMPOTHERFILE $SINGLEINGRDSFILE

sortProducts $SORTEDFILE $PREPFILE

removeBad $SORTEDFILE

splitChunks $SORTEDFILE

createJsons

printf "...all done!\n"
# removes all of the temporary files
# comment this out for testing & debugging
#rm *$OUT_TMP_SUFFIX

