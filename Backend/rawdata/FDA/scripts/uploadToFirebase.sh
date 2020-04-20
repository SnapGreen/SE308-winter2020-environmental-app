#!/bin/bash
SUFFIX_LEN=$(grep -oP "(?<=SUFFIX_LEN:).*" settings.txt)
PRODS_PER_JSON=$(grep -oP "(?<=PRODS_PER_JSON:).*" settings.txt)
FB_WRITES_PER_DAY=$(grep -oP "(?<=FB_WRITES_PER_DAY:).*" settings.txt)
SPLIT_PREFIX=$(grep -oP "(?<=SPLIT_PREFIX:).*" settings.txt)
OUTFILE_END=$(grep -oP "(?<=OUTFILE_END:).*" settings.txt)
UPLOAD_SLEEP=$(grep -oP "(?<=UPLOAD_SLEEP:).*" settings.txt)

function uploadFiles(){
   # uploads a json every 2 seconds
   echo $@
   success=true
   for file in $@
   do
      if [ $success == "true" ] ; then
         num=$(echo $file | grep -o '[0-9]\+')
         logfile=${logdir}/${num}.log
         echo $num $logfile

         curl --header "Content-Type: application/json"\
            --request POST --data  @$file http://localhost:8080/products\
            &> $logfile

         sed -i 's//\n/g' $logfile

         result=$(cat $logfile | grep -o 'successful')
         if [[ $result == "successful" ]] ; then
            echo "$file was succesfully uploaded"
            rm $file
         else
            echo "$file upload was unsuccessful"
            success=false
         fi
         sleep $UPLOAD_SLEEP
      fi
   done
}

prompt=false

if [ $# -eq 0 ] ; then

   echo "This will upload to the server: only one person should be doing it per day."

   read -p 'Are you sure you want to go forward? (y/n): ' response

   if [ $response != "y" ] ; then
      prompt=false
      echo "goodbye!"
   else
      prompt=true
   fi
elif [ $1 == "-b" ] ; then
   prompt=true
fi

if [ $prompt == "true" ] ; then
   max_file_uploads=$((FB_WRITES_PER_DAY / PRODS_PER_JSON))

   shopt -s nullglob

   alljsons=(../$SPLIT_PREFIX*$OUTFILE_END)

   logdir="logs/uploads"

   filesToUpload="${alljsons[@]:0:$max_file_uploads}"

   uploadFiles $filesToUpload
fi
   
