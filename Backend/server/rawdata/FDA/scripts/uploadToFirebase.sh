#!/bin/bash
PRODS_PER_JSON=500
SUFFIX_LEN=4
FB_WRITES_PER_DAY=$(grep -oP "(?<=FB_WRITES_PER_DAY:).*" settings.txt)
SPLIT_PREFIX=$(grep -oP "(?<=SPLIT_PREFIX:).*" settings.txt)
OUT_SUFFIX=$(grep -oP "(?<=OUT_SUFFIX:).*" settings.txt)

#todo:
#max_file_uploads=$((FB_WRITES_PER_DAY / PRODS_PER_JSON))
#
#shopt -s nullglob
#
#alljsons=(../branded_food_*.json)
#
#echo ${alljsons[@]}

#uploadfilenums="${allfilenums[@]:0:$MAX_DAILY_FILE_UPLOADS}"
#echo "$uploadfilenums"
#echo ${uploadfilenums[3]}

#for num in $filearr; 
#do
#   filename="${SPLIT_PREFIX}$num${OUT_SUFFIX}"
#   echo $filename
#done


