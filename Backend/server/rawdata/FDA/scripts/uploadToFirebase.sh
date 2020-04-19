#!/bin/bash
SUFFIX_LEN=$(grep -oP "(?<=SUFFIX_LEN:).*" settings.txt)
PRODS_PER_JSON=$(grep -oP "(?<=PRODS_PER_JSON:).*" settings.txt)
FB_WRITES_PER_DAY=$(grep -oP "(?<=FB_WRITES_PER_DAY:).*" settings.txt)
SPLIT_PREFIX=$(grep -oP "(?<=SPLIT_PREFIX:).*" settings.txt)
OUTFILE_END=$(grep -oP "(?<=OUTFILE_END:).*" settings.txt)


max_file_uploads=$((FB_WRITES_PER_DAY / PRODS_PER_JSON))

shopt -s nullglob

alljsons=(../$SPLIT_PREFIX*$OUTFILE_END)

logdir="logs/uploads"

uploadfiles="${alljsons[@]:0:$max_file_uploads}"

# uploads a json every 2 seconds
for file in $uploadfiles; 
do
   num=$(echo $file | grep -o '[0-9]\+')
   logfile=${logdir}/${num}.log

   curl --header "Content-Type: application/json"\
      --request POST --data  @$file http://localhost:8080/products\
      &> ${logdir}/${num}.log

   result=$(cat $logfile | grep -o 'successful')
   if [[ $result == "successful" ]] ; then
      echo "$file was succesfully uploaded"
      rm $file
   else
      echo "$file upload was unsuccessful"
   fi
   sleep 2
done

