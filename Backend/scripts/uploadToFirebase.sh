#!/bin/bash
SETTINGS="files/settings.txt"
SUFFIX_LEN=$(grep -oP "(?<=^SUFFIX_LEN:).*" $SETTINGS)
PRODS_PER_JSON=$(grep -oP "(?<=^PRODS_PER_JSON:).*" $SETTINGS)
FB_WRITES_PER_DAY=$(grep -oP "(?<=^FB_WRITES_PER_DAY:).*" $SETTINGS)
SPLIT_PREFIX=$(grep -oP "(?<=^SPLIT_PREFIX:).*" $SETTINGS)
OUTFILE_END=$(grep -oP "(?<=^OUTFILE_END:).*" $SETTINGS)
UPLOAD_SLEEP=$(grep -oP "(?<=^UPLOAD_SLEEP:).*" $SETTINGS)
LOGDIR=$(grep -oP "(?<=^LOGDIR:).*" $SETTINGS)
LASTUPLOAD=$(grep -oP "(?<=^LASTUPLOAD:).*" $SETTINGS)
USAGE="\tUsage: ./uploadToFirebase.sh [OPTIONS] (-h for help)\n"
HELP="${USAGE}\tOPTIONS:\n"
HELP="${HELP}\t\t-b: upload without prompt\n"
HELP="${HELP}\t\t-s: print settings only\n"
HELP="${HELP}\t\t-h: print help\n"

fin=false
prompt=true

function checkSettings(){
   echo "settings check:"
   printf "\tSETTINGS: %s\n" "$SETTINGS"
   printf "\tSUFFIX_LEN: %s\n" "$SUFFIX_LEN"
   printf "\tPRODS_PER_JSON: %s\n" "$PRODS_PER_JSON"
   printf "\tFB_WRITES_PER_DAY: %s\n" "$FB_WRITES_PER_DAY"
   printf "\tSPLIT_PREFIX: %s\n" "$SPLIT_PREFIX"
   printf "\tOUTFILE_END: %s\n" "$OUTFILE_END"
   printf "\tUPLOAD_SLEEP: %s\n" "$UPLOAD_SLEEP"
   printf "\tLOGDIR: %s\n" "$LOGDIR"
   printf "\tLASTUPLOAD: %s\n" "$LASTUPLOAD"
   printf "\tUSAGE:\n"
   printf "$USAGE"
   printf "\tHELP:\n"
   printf "$HELP"
}

function uploadFiles(){
   # uploads a json every 2 seconds
   success=true
   for file in $@
   do
      if [ $success == "true" ] ; then
         num=$(echo $file | grep -o '[0-9]\+')
         logfile="${LOGDIR}/${num}.log"

         curl --header "Content-Type: application/json"\
            --request POST --data  @$file http://localhost:8080/products\
            &> $logfile

         sed -i 's//\n/g' $logfile

         result=$(cat $logfile | grep -o 'successful')
         if [[ $result == "successful" ]] ; then
            echo "$file was succesfully uploaded"
            echo "$file" > $LASTUPLOAD
            rm $file
         else
            echo "$file upload was unsuccessful"
            success=false
         fi
         sleep $UPLOAD_SLEEP
      fi
   done
}

if [ $# -ne 0 ] ; then
   if [ "$1" == "-s" ] ; then
      checkSettings
      fin=true
      prompt=false
   elif [ "$1" == "-b" ] ; then
      prompt=false
   elif [ "$1" == "-h" ] ; then
      printf "$HELP"
      fin=true
      prompt=false
   else
      printf "USAGE"
   fi
fi

if [ "$prompt" == "true" ] ; then
   echo "This will upload to the server: only one person should be doing it per day."

   read -p 'Are you sure you want to go forward? (y/n): ' response

   if [ "$response" != "y" ] && [ "$response" != "Y" ] ; then
      printf "You entered %s\n" "$response"
      fin=true
      echo "goodbye!"
   fi
fi

if [ "$fin" == "false" ] ; then
   max_file_uploads=$((FB_WRITES_PER_DAY / PRODS_PER_JSON))

   shopt -s nullglob

   alljsons=(../$SPLIT_PREFIX*$OUTFILE_END)

   filesToUpload="${alljsons[@]:0:$max_file_uploads}"

   uploadFiles $filesToUpload
fi
   
