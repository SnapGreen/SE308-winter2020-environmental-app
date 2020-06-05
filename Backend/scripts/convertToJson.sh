#!/bin/bash
debug=true
silent=false

#https://stackoverflow.com/questions/59895/how-to-get-the-source-directory-of-a-bash-script-from-within-the-script-itself
THISPATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd)"
SETTINGS="${THISPATH}/settings.txt"

if [ $# != 0 ] ; then
   if [ "$1" == "-ns" ] || [ "$1" == "-n" ] || [ "$1" == "-t" ] ; then
      silent=true
      if [ "$1" == "-ns" ] || [ "$1" == "-n" ] ; then
         #SETTINGS="${THISPATH}/settings_npm.txt"
         SETTINGS="Backend/scripts/settings_npm.txt"
      fi
   fi
fi

AWKDIR=$(grep -oP '(?<=^AWKDIR:).*' $SETTINGS)
DATADIR=$(grep -oP '(?<=^DATADIR:).*' $SETTINGS)
FDADIR=$(grep -oP '(?<=^FDADIR:).*' $SETTINGS)
LOGDIR=$(grep -oP '(?<=^LOGDIR:).*' $SETTINGS)
RAWDATADIR=$(grep -oP '(?<=^RAWDATADIR:).*' $SETTINGS)
TESTDIR=$(grep -oP '(?<=^TESTDIR:).*' $SETTINGS)
TMPDIR=$(grep -oP '(?<=^TMPDIR:).*' $SETTINGS)
FDADATASOURCE=$(grep -oP '(?<=^FDADATASOURCE:).*' $SETTINGS)
FDAINDIR="${RAWDATADIR}${FDADIR}"
FDAOUTDIR="${DATADIR}${FDADIR}"
FDAINFILE="${FDAINDIR}${FDADATASOURCE}"
MAPFILE=$(grep -oP '(?<=^MAPFILE:).*' $SETTINGS)
PRODS_PER_JSON=$(grep -oP '(?<=^PRODS_PER_JSON:).*' $SETTINGS)
FDA_PATTERNFILE=$(grep -oP '(?<=^FDA_PATTERNFILE:).*' $SETTINGS)
SPLIT_PREFIX=$(grep -oP '(?<=^SPLIT_PREFIX:).*' $SETTINGS)
SUFFIX_LEN=$(grep -oP '(?<=^SUFFIX_LEN:).*' $SETTINGS)
OUTFILE_END=$(grep -oP '(?<=^OUTFILE_END:).*' $SETTINGS)
TMPFILE_END=$(grep -oP '(?<=^TMPFILE_END:).*' $SETTINGS)
NUM_CLEAN_TESTS=$(grep -oP '(?<=^NUM_CLEAN_TESTS:).*' $SETTINGS)
AWK_FORMAT="${AWKDIR}csvtojson.awk"
AWK_SHRINK="${AWKDIR}consolidateIngreds.awk"
AWK_TRIM="${AWKDIR}trim.awk"
AWK_RM_INGRDS="${AWKDIR}removeIngreds.awk"
AWK_MAP="${AWKDIR}map_fdcid_gtin.awk"
NOCATS_TMP="${TMPDIR}nocats.tmp"
PREPPED_TMP="${TMPDIR}prepped.tmp"
INGREDIENTS_TMP="${TMPDIR}ingredients.tmp"
INGREDIENTSB4_TMP="${TMPDIR}ingredientsb4.tmp"
NON_INGREDIENTS_TMP="${TMPDIR}non_ingredients.tmp"
SET_INGREDIENTS_TMP="${TMPDIR}set_ingredients.tmp"
RELEVANTDATA_TMP="${TMPDIR}relevantdata.tmp"
RELEVANTSORTED_TMP="${TMPDIR}relevantsorted.tmp"
RELEVANTTRIMMED_TMP="${TMPDIR}relevanttrimmed.tmp"
RELEVANTDATASORTED_TMP="${TMPDIR}relevantdatasorted.tmp"
CLEANTEST_TMP="${TMPDIR}longestlines.tmp"
JOINED_TMP="${TMPDIR}joined.tmp"
SORTED_TMP="${TMPDIR}sorted.tmp"
USAGE="\t\tUsage: ./convertToJson.sh [OPTION] (-h for help)\n"
HELP="${USAGE}\t\tOPTIONS:\n"
HELP="${HELP}\t\t\t-b: bypass debug mode\n"
HELP="${HELP}\t\t\t-n: run with settings relative to repo root\n"
HELP="${HELP}\t\t\t-ns: print settings relative to repo root only\n"
HELP="${HELP}\t\t\t-s: print settings only\n"
HELP="${HELP}\t\t\t-t: test mode (silent)\n"
HELP="${HELP}\t\t\t-h: print help\n"
HELP="${HELP}\t\t**debug mode: leave temp files undeleted**\n"
HELP="${HELP}\t\t**debug mode is ON if no OPTION specified\n"


#https://stackoverflow.com/questions/17066250/create-timestamp-variable-in-bash-script
function timestamp(){
   date +%s
}

#function findWorkingDirectories(){
#   if [ -z ${FDAOUTDIR} ] ; then
#      work_out_dirs=$FDAOUTDIR
#      work_in_dirs=$FDAINDIR
#   else
#      work_out_dirs=$($FDAOUTDIR*/)
#      work_in_dirs=$($FDAINDIR*/)
#      for dir in $work_out_dirs; do
#         if [ -n $dir ] ; then
#            work_out_dirs=${work_out_dirs[@]/$dir} 
#            indir=$(echo $dir | grep -o "\[0-9]\+\/")
#            indir="${FDAINDIR}${indir}"
#            work_in_dirs=${work_in_dirs[@]/$indir} 
#         fi
#      done
#   fi
#   if [ -z $work_out_dirs ] ; then
#      fin=true
#   fi
#}

function checkSettings(){
   # function to verify settings are what we expect (for debugging)
   echo "settings check:"
   printf "\tSETTINGS: %s\n" "$SETTINGS"
   printf "\tAWKDIR: %s\n" "$AWKDIR"
   printf "\tDATADIR: %s\n" "$DATADIR"
   printf "\tFDADIR: %s\n" "$FDADIR"
   printf "\tFDAINDIR: %s\n" "$FDAINDIR"
   printf "\tFDAOUTDIR: %s\n" "$FDAOUTDIR"
   printf "\tLOGDIR: %s\n" "$LOGDIR"
   printf "\tRAWDATADIR: %s\n" "$RAWDATADIR"
   printf "\tTMPDIR: %s\n" "$TMPDIR"
   printf "\tTESTDIR: %s\n" "$TESTDIR"
   printf "\tFDAINFILE: %s\n" "$FDAINFILE"
   printf "\tMAPFILE: %s\n" "$MAPFILE"
   printf "\tPRODS_PER_JSON: %s\n" "$PRODS_PER_JSON"
   printf "\tFDA_PATTERNFILE: %s\n" "$FDA_PATTERNFILE"
   printf "\tSPLIT_PREFIX: %s\n" "$SPLIT_PREFIX"
   printf "\tSUFFIX_LEN: %s\n" "$SUFFIX_LEN"
   printf "\tOUTFILE_END: %s\n" "$OUTFILE_END"
   printf "\tTMPFILE_END: %s\n" "$TMPFILE_END"
   printf "\tNUM_CLEAN_TESTS: %s\n" "$NUM_CLEAN_TESTS"
	printf "\tAWK_FORMAT: %s\n" "$AWK_FORMAT"
	printf "\tAWK_SHRINK: %s\n" "$AWK_SHRINK"
	printf "\tAWK_TRIM: %s\n" "$AWK_TRIM"
	printf "\tAWK_RM_INGRDS: %s\n" "$AWK_RM_INGRDS"
	printf "\tAWK_MAP: %s\n" "$AWK_MAP"
	printf "\tNOCATS_TMP: %s\n" "$NOCATS_TMP"
	printf "\tPREPPED_TMP: %s\n" "$PREPPED_TMP"
	printf "\tINGREDIENTS_TMP: %s\n" "$INGREDIENTS_TMP"
	printf "\tINGREDIENTSB4_TMP: %s\n" $INGREDIENTSB4_TMP
	printf "\tNON_INGREDIENTS_TMP: %s\n" "$NON_INGREDIENTS_TMP"
	printf "\tSET_INGREDIENTS_TMP: %s\n" "$SET_INGREDIENTS_TMP"
	printf "\tRELEVANTDATA_TMP: %s\n" "$RELEVANTDATA_TMP"
	printf "\tRELEVANTSORTED_TMP: %s\n" "$RELEVANTSORTED_TMP"
	printf "\tRELEVANTTRIMMED_TMP: %s\n" "$RELEVANTTRIMMED_TMP"
	printf "\tCLEANTEST_TMP: %s\n" "$CLEANTEST_TMP"
	printf "\tJOINED_TMP: %s\n" "$JOINED_TMP"
	printf "\tSORTED_TMP: %s\n" "$SORTED_TMP"
	printf "\tUSAGE:\n" 
	printf "$USAGE" 
	printf "\tHELP:\n" 
	printf "$HELP" 
}

if [ $# != 0 ] ; then
   if [ $1 == "-ns" ] || [ "$1" == "-s" ] ; then
      checkSettings
      exit 0
   fi
fi

function prepData(){
   if [[ "$silent" == "false" ]] ; then
      printf "prepping data...\n"
   fi

   # makes sure any Windows <CR> are converted to linux \r
   dos2unix $1

   # lines like these create the files if they don't exist, and clear them if
   # they do exist
   > $2
   > $3

   if [[ "$silent" == "false" ]] ; then
      printf "removing category line...\n"
   fi

   # removes the first line of the input file and saves to a temp file
   # https://stackoverflow.com/questions/339483/how-can-i-remove-the-first-line-of-a-text-file-using-bash-sed-script
   tail -n +2 $1 > $2

   if [[ "$silent" == "false" ]] ; then
      printf "removing malformed lines...\n"
   fi

   # adapted from Socowi's answer:
   # https://stackoverflow.com/questions/57167920/sed-awk-remove-newline-with-condition
   perl -np0 -e 's/\n(?!")//g' $2 > $3

   if [ $debug == "off" ] ; then
      rm $2
   fi
}

function extractRelevantCols(){
   if [[ "$silent" == "false" ]] ; then
      printf "extracting relevant columns...\n"
   fi

   > $3
   # created a reduced file consisting only of the parts we need
   awk -f $1 $2 >> $3

   if [ $debug == "off" ] ; then
      rm $2
   fi
}

function sortRelevantData(){
   if [[ "$silent" == "false" ]] ; then
      printf "sorting FDA entries...\n"
   fi

   # sorts the file from highest gtin to the lowest 
   # this prepares the file for removal of duplicate entries, saving only the
   # newest
   >$2
   sort -t '|' -nrk2 $1 >> $2

   if [ $debug == "off" ] ; then
      rm $1
   fi
}

function removeOlderData(){
   if [[ "$silent" == "false" ]] ; then
      printf "removing older entries...\n"
   fi

   uniq -s 7 -w 14 $1 $2
}

function createCleanTests(){
   if [[ "$silent" == "false" ]] ; then
      printf "creating clean tests...\n"
   fi

   # selects the products with the longest lists of ingredients
   # number of products selected = NUM_CLEAN_TESTS
   # places them separately in tests/clean/
   # expected versions need to be manually written out
   testOutPrefix="${TESTDIR}clean/"
   testOutEnd=".txt"
   > $CLEANTEST_TMP

   awk '{printf("%d\t%s\n",length, $0)}' $1 | sort -nr | head -$NUM_CLEAN_TESTS\
      >> $CLEANTEST_TMP

   num=1
   while [[ $num -le $NUM_CLEAN_TESTS ]] ;
   do
      outTestFile="${testOutPrefix}in${num}${testOutEnd}"
      > $outTestFile
      tail -n $num $CLEANTEST_TMP | head -n 1 | cut -f 2 >> $outTestFile
      num=$((num+1))
   done
}

function createMap(){
   if [[ "$silent" == "false" ]] ; then
      printf "creating fdcid-gtin map...\n"
   fi

   # clear the map file
   > $3
   # creates a map of fdc-id : gtin14 pairs
   # we need this to track FDA updates and modify them in the db
   # alternately, we could store it in the db
   awk -f $1 $2 >> $3
}

function isolateIngredients(){
   if [[ "$silent" == "false" ]] ; then
      printf "splitting ingredients/non-ingredients...\n"
   fi

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
   if [[ "$silent" == "false" ]] ; then
      printf "transforming ingredients (takes time)...\n"
   fi


   if [[ $debug == "true" ]] ; then
      # resets the ingredients file before starting
      # if debug mode is off, the copy likely isn't in the directory 
      cat $INGREDIENTSB4_TMP > $2
      # write the timestamp in the log
      starttime=$(timestamp)
      logfile="${LOGDIR}cleans/cleaned${starttime}.log"
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
      if [[ "$silent" == "false" ]] ; then
         printf "elapsed: %d seconds\n" $elapsed >> $logfile
      fi

   else
      # apply removal patterns to ingredients
      while read -r pattern;
      do
         if [[ ${pattern:0:1} != "#" ]] ; then
            sed -i "$pattern" $2
         elif [[ "$silent" == "false" ]] ; then
            printf "skipping %s\n" "${pattern:1}"
         fi
      done < $1
   fi

}

function consolidateIngredients(){
   if [[ "$silent" == "false" ]] ; then
      printf "consolidating ingredients into sets...\n"
   fi

   # remove ingredients mentioned more than once
   > $3
   awk -f $1 $2 >> $3
}

function rejoinFiles(){
   if [[ "$silent" == "false" ]] ; then
      printf "combining non-ingredient data with ingredient sets...\n"
   fi

   # add ingredients back to other data
   # final format should be gtin|modifiedDate|ingreds
   > $3
   paste -d "|" $1 $2 >> $3

   if [ $debug == "off" ] ; then
      rm $1 $2
   fi
} 

function sortProducts(){
   if [[ "$silent" == "false" ]] ; then
      printf "sorting products...\n"
   fi

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
      if [[ "$silent" == "false" ]] ; then
         printf "removing $removenum entries...\n"
      fi

   else
      if [[ "$silent" == "false" ]] ; then
         printf "no bad entries found...\n"
      fi

   fi

   sed -i '/^00000000000000/d' $1
}

function splitChunks(){
   if [[ "$silent" == "false" ]] ; then
      printf "splitting int %d line chunks...\n" $PRODS_PER_JSON
   fi

   # splits the trimmed file
   # -l flag denotes we want to split files by $PRODS_PER_JSON number of lines
   # -d means we want digit suffixes
   # -a $SUFFIX_LEN means we want $SUFFIX_LEN digits in each suffix
   # -e means don't output zero size files
   # $TMPDIR is the directory where the split files will be placed
   # $SPLIT_PREFIX is the first part of what each file will be named
   # --additional-suffix will be added to the end
   split -l $PRODS_PER_JSON -d  -e  --additional-suffix=$TMPFILE_END\
      -a $SUFFIX_LEN $1 "${TMPDIR}${SPLIT_PREFIX}"

   if [ $debug == "off" ] ; then
      rm $1
   fi
}

function createJsons(){
   if [[ "$silent" == "false" ]] ; then
      printf "turning chunks into JSON files (get comfy)...\n"
   fi

   files="${TMPDIR}${SPLIT_PREFIX}[0-9]*.tmp"
   for file in $files
   do
      # extracting the suffix number from each temp file
      outnum=$(echo $file | grep -oP "[0-9]{$SUFFIX_LEN}(?=.tmp)")
      # putting together an output file name
      outname="${FDAOUTDIR}${SPLIT_PREFIX}"$outnum"${OUTFILE_END}"
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


if [ $# != 0 ] ; then
   if [ $1 == "-b" ] ; then
      debug="off"
      if [[ "$silent" == "false" ]] ; then
         echo "debug mode off"
      fi
   elif [ $1 == "-h" ] ; then
      printf "$HELP"
      exit 0
   else
      printf "$USAGE"
      exit 1
   fi
fi

#findWorkingDirectories

if [[ "$debug" == "true" ]] ; then
   checkSettings
fi

prepData $FDAINFILE $NOCATS_TMP $PREPPED_TMP
extractRelevantCols $AWK_TRIM $PREPPED_TMP $RELEVANTDATA_TMP
sortRelevantData $RELEVANTDATA_TMP $RELEVANTSORTED_TMP
removeOlderData $RELEVANTSORTED_TMP $RELEVANTTRIMMED_TMP
createMap $AWK_MAP $RELEVANTTRIMMED_TMP $MAPFILE 
isolateIngredients $AWK_RM_INGRDS $RELEVANTTRIMMED_TMP $NON_INGREDIENTS_TMP $INGREDIENTS_TMP 

if [[ $debug == "true" ]] ; then
   createCleanTests $INGREDIENTSB4_TMP
fi

cleanIngredients $FDA_PATTERNFILE $INGREDIENTS_TMP 
consolidateIngredients $AWK_SHRINK $INGREDIENTS_TMP $SET_INGREDIENTS_TMP 
rejoinFiles $NON_INGREDIENTS_TMP $SET_INGREDIENTS_TMP $JOINED_TMP
sortProducts $JOINED_TMP $SORTED_TMP
removeBad $SORTED_TMP
splitChunks $SORTED_TMP
createJsons $AWK_FORMAT

if [ $debug == "off" ] ; then
   # removes all of the temporary files
   rm "${TMPDIR}*$TMPFILE_END"
fi

# removes files that sed produces, if any
find "$TMPDIR" -regextype sed -regex ".*/sed[a-zA-Z0-9]\{6\}" -delete

if [[ "$silent" == "false" ]] ; then
   printf "...all done!\n"
fi

