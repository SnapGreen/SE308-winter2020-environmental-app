#!/bin/bash
INFILE="branded_food.csv"
TMPFILE="branded_food.tmp"
OUTFILE="branded_food.json"
AWKCMDFILE="command.awk"

# clears the output file of any data
> $OUTFILE

# removes the first line of the input file and saves to a temp file
# https://stackoverflow.com/questions/339483/how-can-i-remove-the-first-line-of-a-text-file-using-bash-sed-script
tail -n +2 "$INFILE" > "$TMPFILE" 

# first line into the file
echo "{\"gtin14ToIngredients\":[" >> $OUTFILE 

# adds all of the gtin/ingredient pairs
awk -f $AWKCMDFILE $TMPFILE >> $OUTFILE 

# removes the last comma from the last line
truncate -s-2 $OUTFILE

# adds closing characters to json file
printf "\n]}\n" >> $OUTFILE

> $TMPFILE

