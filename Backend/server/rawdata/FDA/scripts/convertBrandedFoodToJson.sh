#!/bin/bash
INFILE="../branded_food.csv"
TMPFILE="branded_food.tmp"
TMPINGREDSFILE="ingredients.tmp"
PATTERNFILE="removalpatterns.txt"
TRIMFILE="branded_food_trim.tmp"
SPLIT_PREFIX="branded_food_"
OUT_TMP_SUFFIX=".tmp"
OUT_SUFFIX=".json"
SPLIT_FILE_LINES="500"
AWKFORMATCMD="command.awk"
AWKTRIMCMD="trim.awk"
AWKMAPCMD="map_fdcid-gtin.awk"
AWKISOLATEINGRDSCMD="isolate_ingreds_cmd.awk"
MAPFILE="../fdcid-gtin.txt"

function extractRelevantCols(){
   > $3
   # created a reduced file consisting only of the parts we need
   awk -f $1 $2 >> $3
   rm $2
}

function createMap(){
   # clear the map file
   > $1
   # creates a map of fdc-id : gtin14 pairs
   # we need this to track FDA updates and modify them in the db
   # alternately, we could store it in the db
   awk -f $2 $3 >> $1
}

function isolateIngredients(){
   # clear the temp ingreds file if it exists
   > $1
   # create a separate file with ingredients only for sed to work on quickly
   awk -f $2 $3 >> $1
}

function cleanIngredients(){
   # apply removal patterns to ingredients
   while read -r pattern;
   do
      sed -i "$pattern" $1
   done < $2
}


# removes the first line of the input file and saves to a temp file
# https://stackoverflow.com/questions/339483/how-can-i-remove-the-first-line-of-a-text-file-using-bash-sed-script
tail -n +2 "$INFILE" > "$TMPFILE" 

extractRelevantCols $AWKTRIMCMD $TMPFILE $TRIMFILE

createMap $MAPFILE $AWKMAPCMD $TRIMFILE

isolateIngredients $TMPINGREDSFILE $AWKISOLATEINGRDSCMD $TRIMFILE

cleanIngredients $TMPINGREDSFILE $PATTERNFILE

# splits the trimmed file
# -l flag denotes we want to split files by $SPLIT_FILE_LINES number of lines
# -d means we want digit suffixes
# -a 4 means we want 4 digits in each suffix
# -e means don't output zero size files
# $SPLIT_PREFIX is the first part of what each file will be named
# --additional-suffix will be added to the end
#split -l $SPLIT_FILE_LINES -d  -e $TRIMFILE $SPLIT_PREFIX \
#   --additional-suffix=$OUT_TMP_SUFFIX -a 4

#erase the unsplit trimmed file
#rm $TRIMFILE

#for file in *.tmp
#do
#   # extracting the suffix number from each temp file
#   outnum=$(echo $file | egrep -o [0-9]+)
#   # putting together an output file name
#   outname="../${SPLIT_PREFIX}${outnum}${OUT_SUFFIX}"
#   # make sure the outfile is clear, if it exists
#   > $outname
#   # place the first line into the outfile
#   echo "\"products\": [" >> $outname
#   # run awk to process the rest
#   awk -f $AWKFORMATCMD $file >> $outname
#   # removes comma from last line of last entry
#   sed -i '$s/,$//' $outname
#   #adds closing bracket to outfile
#   echo "]" >> $outname
#done

# removes all of the temporary files
#rm *.tmp

