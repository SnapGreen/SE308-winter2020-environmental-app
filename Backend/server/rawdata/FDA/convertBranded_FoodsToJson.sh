#!/bin/bash
INFILE="branded_food.csv"
TMPFILE="branded_food.tmp"
<<<<<<< HEAD
SPLIT_PREFIX="branded_food_"
OUT_TMP_SUFFIX=".tmp"
OUT_SUFFIX=".json"
SPLIT_FILE_LINES="15000"
AWKCMDFILE="command.awk"

# clears the output file of any data
#> $OUTFILE
=======
OUTFILE="branded_food.json"
AWKCMDFILE="command.awk"

# clears the output file of any data
> $OUTFILE
>>>>>>> ebfb21a0737a49cfa88dc05c6051a8eb35846c9c

# removes the first line of the input file and saves to a temp file
# https://stackoverflow.com/questions/339483/how-can-i-remove-the-first-line-of-a-text-file-using-bash-sed-script
tail -n +2 "$INFILE" > "$TMPFILE" 

<<<<<<< HEAD
# splits the temp file
# -l flag denotes we want to split by number of lines
# -d means we want the suffixes to use digits (default 2)
# $SPLIT_PREFIX is the first part of what each file will be named
# -additional-suffix will be added to the end
split -l $SPLIT_FILE_LINES -d $TMPFILE $SPLIT_PREFIX \
   --additional-suffix=$OUT_TMP_SUFFIX

#erase the unsplit temporary file
rm $TMPFILE

for file in *.tmp
do
   outnum=$(echo $file | egrep -o [0-9]+)
   outname="${SPLIT_PREFIX}${outnum}${OUT_SUFFIX}"
   > $outname
   echo "\"products\": [" >> $outname
   awk -f $AWKCMDFILE $file >> $outname
   # removes comma from last line of last entry
   sed -i '$s/,$//' $outname
   #adds closing bracket
   echo "]" >> $outname
   rm $file
done
=======
# first line into the file
echo "{\"gtin14ToIngredients\":[" >> $OUTFILE 

# adds all of the gtin/ingredient pairs
awk -f $AWKCMDFILE $TMPFILE >> $OUTFILE 

# removes the last comma from the last line
truncate -s-2 $OUTFILE

# adds closing characters to json file
printf "\n]}\n" >> $OUTFILE

> $TMPFILE
>>>>>>> ebfb21a0737a49cfa88dc05c6051a8eb35846c9c

