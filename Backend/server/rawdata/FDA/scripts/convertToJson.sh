#!/bin/bash
INFILE=$(grep -oP '(?<=INFILE:).*' settings.txt)
MAPFILE=$(grep -oP '(?<=MAPFILE:).*' settings.txt)
PRODS_PER_JSON=$(grep -oP '(?<=PRODS_PER_JSON:).*' settings.txt)
PATTERNFILE=$(grep -oP '(?<=PATTERNFILE:).*' settings.txt)
SPLIT_PREFIX=$(grep -oP '(?<=SPLIT_PREFIX:).*' settings.txt)
SUFFIX_LEN=$(grep -oP '(?<=SUFFIX_LEN:).*' settings.txt)
OUTFILE_END=$(grep -oP '(?<=OUTFILE_END:).*' settings.txt)
TMPFILE_END=$(grep -oP '(?<=TMPFILE_END:).*' settings.txt)
NUM_CLEAN_TESTS=$(grep -oP '(?<=NUM_CLEAN_TESTS:).*' settings.txt)
NOCATS_TMP="nocats.tmp"
PREPPED_TMP="prepped.tmp"
INGREDIENTS_TMP="ingredients.tmp"
INGREDIENTSB4_TMP="ingredientsb4.tmp"
NON_INGREDIENTS_TMP="non_ingredients.tmp"
SET_INGREDIENTS_TMP="set_ingredients.tmp"
RELEVANTDATA_TMP="relevantdata.tmp"
CLEANTEST_TMP="longestlines.tmp"
JOINED_TMP="joined.tmp"
SORTED_TMP="sorted.tmp"
AWK_FORMAT="csvtojson.awk"
AWK_SHRINK="consolidateIngreds.awk"
AWK_TRIM="trim.awk"
AWK_RM_INGRDS="removeIngreds.awk"
AWK_MAP="map_fdcid_gtin.awk"

debug="on"

if [ $# != 0 ] && [ $1 == "-f" ] ; then
   echo "debug mode off"
   debug="off"
fi

#https://stackoverflow.com/questions/17066250/create-timestamp-variable-in-bash-script
function timestamp(){
   date +%s
}

function checkSettings(){
   # function to verify settings are what we expect (for debugging)
   printf "settings check...\n"
   printf "\tINFILE: %s\n" $INFILE
   printf "\tMAPFILE: %s\n" $MAPFILE
   printf "\tPRODS_PER_JSON: %s\n" $PRODS_PER_JSON
   printf "\tPATTERNFILE: %s\n" $PATTERNFILE
   printf "\tSPLIT_PREFIX: %s\n" $SPLIT_PREFIX
   printf "\tSUFFIX_LEN: %s\n" $SUFFIX_LEN
   printf "\tOUTFILE_END: %s\n" $OUTFILE_END
   printf "\tTMPFILE_END: %s\n" $TMPFILE_END
   printf "\tNUM_CLEAN_TESTS: %s\n" $NUM_CLEAN_TESTS
}

function prepData(){
   printf "prepping data...\n"
   # makes sure any Windows <CR> are converted to linux \r
   dos2unix $1

   # commands like these will create the args as files, or clear them if they
   # already exist.  
   > $2
   > $3

   printf "removing category line...\n"
   # removes the first line of the input file and saves to a temp file
   # https://stackoverflow.com/questions/339483/how-can-i-remove-the-first-line-of-a-text-file-using-bash-sed-script
   tail -n +2 $1 > $2

   printf "removing malformed lines...\n"
   # adapted from Socowi's answer:
   # https://stackoverflow.com/questions/57167920/sed-awk-remove-newline-with-condition
   perl -np0 -e 's/\n(?!")//g' $2 > $3

   if [ $debug == "off" ] ; then
      rm $2
   fi
}

function extractRelevantCols(){
   printf "extracting relevant columns...\n"
   > $3
   # created a reduced file consisting only of the parts we need
   awk -f $1 $2 >> $3

   if [ $debug == "off" ] ; then
      rm $2
   fi
}

function createCleanTests(){
   printf "creating clean tests...\n"
   # selects the products with the longest lists of ingredients
   # number of products selected = NUM_CLEAN_TESTS
   # places them separately in tests/clean/
   # expected versions need to be manually written out
   testOutPrefix="tests/clean/"
   testOutEnd=".test"
   tmpCleanFile="${testOutPrefix}${CLEANTEST_TMP}"
   > $tmpCleanFile

   awk '{printf("%d\t%s\n",length, $0)}' $1 | sort -nr | head -$NUM_CLEAN_TESTS\
      >> $tmpCleanFile

   num=1
   while [[ $num -le $NUM_CLEAN_TESTS ]] ;
   do
      outTestFile="${testOutPrefix}in${num}${testOutEnd}"
      > $outTestFile
      tail -n $num $tmpCleanFile | head -n 1 | cut -f 2 >> $outTestFile
      num=$((num+1))
   done

   rm $tmpCleanFile
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

   if [ $debug == "off" ] ; then
      rm $2
   else
      # makes a copy for comparison & resetting
      cat $4 > $INGREDIENTSB4_TMP
   fi
}

function cleanIngredients(){
   printf "transforming ingredients (takes time)...\n"

   if [[ $debug == "on" ]] ; then
      # resets the ingredients file before starting
      # if debug mode is off, the copy likely isn't in the directory 
      cat $INGREDIENTSB4_TMP > $2
      # write the timestamp in the log
      starttime=$(timestamp)
      logfile="logs/cleans/cleaned${starttime}.log"
      # apply removal patterns to ingredients
      after=$(wc -c < $2)
      while read -r pattern;
      do
         # this allows us to comment out patterns in removalpatterns.txt
         if [[ ${pattern:0:1} != "#" ]] ; then
            before=$after
            sed -i "$pattern" $2 
            after=$(wc -c < $2)
            removed=$((before - after))
            printf "\tpattern %s removed %s characters\n" "$pattern" "$removed"\
               | tee -a $logfile
         else
            printf "\tskipping %s\n" "${pattern:1}" | tee -a $logfile
         fi
      done < $1
      endtime=$(timestamp)
      elapsed=$((endtime - starttime))
      printf "elapsed: %d seconds\n" $elapsed >> $logfile
   else
      # apply removal patterns to ingredients
      while read -r pattern;
      do
         if [[ ${pattern:0:1} != "#" ]] ; then
            sed -i "$pattern" $2
         else
            printf "skipping %s\n" "${pattern:1}"
         fi
      done < $1
   fi

}

function consolidateIngredients(){
   printf "consolidating ingredients into sets...\n"
   # remove ingredients mentioned more than once
   > $3
   awk -f $1 $2 >> $3
}

function rejoinFiles(){
   printf "combining non-ingredient data with ingredient sets...\n"
   # add ingredients back to other data
   # final format should be gtin|modifiedDate|ingreds
   > $3
   paste -d "|" $1 $2 >> $3

   if [ $debug == "off" ] ; then
      rm $1 $2
   fi
} 

function sortProducts(){
   printf "sorting products...\n"
   # sorts the file from lowest gtin to highest
   # this will help to keep things orderly in case something goes wrong
   >$2
   sort -t ',' -nk1 $1 >> $2

   if [ $debug == "off" ] ; then
      rm $1
   fi
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

   if [ $debug == "off" ] ; then
      rm $1
   fi
}

function createJsons(){
   printf "turning chunks into JSON files (get comfy)...\n"
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
      printf "\t\"products\": [\n" >> $outname
      # run awk to process the rest
      awk -f $1 $file >> $outname
      # removes comma from last line of last entry
      sed -i '$s/,$//' $outname
      #adds closing bracket to outfile
      printf "\t]\n" >> $outname
      echo "}" >> $outname
   done
}

if [[ $debug == "on" ]] ; then
   checkSettings
fi

prepData $INFILE $NOCATS_TMP $PREPPED_TMP

extractRelevantCols $AWK_TRIM $PREPPED_TMP $RELEVANTDATA_TMP

createMap $AWK_MAP $RELEVANTDATA_TMP $MAPFILE 

isolateIngredients $AWK_RM_INGRDS $RELEVANTDATA_TMP $NON_INGREDIENTS_TMP $INGREDIENTS_TMP 

if [[ $debug == "on" ]] ; then
   createCleanTests $INGREDIENTSB4_TMP
fi

cleanIngredients $PATTERNFILE $INGREDIENTS_TMP 

consolidateIngredients $AWK_SHRINK $INGREDIENTS_TMP $SET_INGREDIENTS_TMP 

rejoinFiles $NON_INGREDIENTS_TMP $SET_INGREDIENTS_TMP $JOINED_TMP

sortProducts $JOINED_TMP $SORTED_TMP

removeBad $SORTED_TMP

splitChunks $SORTED_TMP

createJsons $AWK_FORMAT

if [ $debug == "off" ] ; then
   # removes all of the temporary files
   # comment this out for testing & debugging
   rm *$TMPFILE_END
fi

# removes files that sed produces, if any
find .. -maxdepth 2 -regextype sed -regex ".*/sed[a-zA-Z0-9]\{6\}" -delete

printf "...all done!\n"


