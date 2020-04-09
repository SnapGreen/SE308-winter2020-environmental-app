#!/bin/bash
INFILE="branded_food.csv"
TMPFILE="branded_food.tmp"
SPLIT_PREFIX="branded_food_"
OUT_TMP_SUFFIX=".tmp"
OUT_SUFFIX=".json"
SPLIT_FILE_LINES="500"
AWKCMDFILE="command.awk"

# removes the first line of the input file and saves to a temp file
# https://stackoverflow.com/questions/339483/how-can-i-remove-the-first-line-of-a-text-file-using-bash-sed-script
tail -n +2 "$INFILE" > "$TMPFILE" 

# splits the temp file
# -l flag denotes we want to split files by $SPLIT_FILE_LINES number of lines
# -d means we want digit suffixes
# -a 4 means we want 4 digits in each suffix
# -e means don't output zero size files
# $SPLIT_PREFIX is the first part of what each file will be named
# --additional-suffix will be added to the end
split -l $SPLIT_FILE_LINES -d  -e $TMPFILE $SPLIT_PREFIX \
   --additional-suffix=$OUT_TMP_SUFFIX -a 4

#erase the unsplit temporary file
rm $TMPFILE

for file in *.tmp
do
   # extracting the suffix number from each temp file
   outnum=$(echo $file | egrep -o [0-9]+)
   # putting together an output file name
   outname="${SPLIT_PREFIX}${outnum}${OUT_SUFFIX}"
   # make sure the outfile is clear, if it exists
   > $outname
   # place the first line into the outfile
   echo "\"products\": [" >> $outname
   # run awk to process the rest
   awk -f $AWKCMDFILE $file >> $outname
   # removes comma from last line of last entry
   sed -i '$s/,$//' $outname
   #adds closing bracket to outfile
   echo "]" >> $outname
done

# removes all of the temporary files
rm *.tmp

