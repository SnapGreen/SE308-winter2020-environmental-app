#!/bin/bash
FDC_API="5QRYu6lXusqbS7pRXiXnbtNGvsR7dblDYZFPR1IN"
TMPFILE="links.tmp"
FILELIST="available_data.txt"

FDC_DIR_ADDRESS="https://fdc.nal.usda.gov/fdc-datasets"

#wget -O $TMPFILE -np $FDC_DIR_ADDRESS

sed '/branded_food/!d' $TMPFILE > $FILELIST
sed -i 's/^.*\">//g' $FILELIST 
sed -i 's/<\/a>\s\+/|/g' $FILELIST 
sed -i 's/:\([0-9][0-9]\).*/:\1/g' $FILELIST 
