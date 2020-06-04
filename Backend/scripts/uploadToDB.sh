#!/bin/bash

fin=false
silent=false

THISPATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd)"
SETTINGS="${THISPATH}/settings.txt"

if [ $# -ne 0 ] ; then
   if [ "$1" == "-t" ] || [ "$1" == "-n" ] || [ "$1" == "-ns" ] ; then
      silent=true
      if [ "$1" == "-n" ] || [ "$1" == "-ns" ] ; then
         SETTINGS="${THISPATH}/settings_npm.txt"
      fi
   fi
fi


DATADIR=$(grep -oP "(?<=^DATADIR:).*" $SETTINGS)
FDADIR=$(grep -oP "(?<=^FDADIR:).*" $SETTINGS)
FDADATADIR="${DATADIR}${FDADIR}"
SUFFIX_LEN=$(grep -oP "(?<=^SUFFIX_LEN:).*" $SETTINGS)
PRODS_PER_JSON=$(grep -oP "(?<=^PRODS_PER_JSON:).*" $SETTINGS)
FB_WRITES_PER_DAY=$(grep -oP "(?<=^FB_WRITES_PER_DAY:).*" $SETTINGS)
TESTDIR=$(grep -oP "(?<=TESTDIR).*" $SETTINGS)
TESTSETTINGSDIR=$"${TESTDIR}settings/"
TEST_POPDB_SETTINGS="${TESTSETTINGSDIR}populateDB_settings.txt"
TEST_UPLOADTODB_SETTINGS="${TESTSETTINGSDIR}uploadToDB_settings.txt"
SPLIT_PREFIX=$(grep -oP "(?<=^SPLIT_PREFIX:).*" $SETTINGS)
OUTFILE_END=$(grep -oP "(?<=^OUTFILE_END:).*" $SETTINGS)
UPLOAD_SLEEP=$(grep -oP "(?<=^UPLOAD_SLEEP:).*" $SETTINGS)
LOGDIR=$(grep -oP "(?<=^LOGDIR:).*" $SETTINGS)
UPLOADLOGDIR="${LOGDIR}uploads/"
LASTUPLOAD=$(grep -oP "(?<=^LASTUPLOAD:).*" $SETTINGS)
SERVER_POPULATED=$(grep -oP "(?<=^SERVER_POPULATED:).*" $SETTINGS)
DONE_UPLOADING=$(grep -oP "(?<=^DONE_UPLOADING:).*" $SETTINGS)
USAGE="\t\tUsage: ./uploadToDB.sh [OPTIONS] (-h for help)\n"
HELP="${USAGE}\t\tOPTIONS:\n"
HELP="${HELP}\t\t\t-b: upload without prompt\n"
HELP="${HELP}\t\t\t-n: test with settings relative to repo root\n"
HELP="${HELP}\t\t\t-ns: print settings relative to repo root only\n"
HELP="${HELP}\t\t\t-s: print settings only\n"
HELP="${HELP}\t\t\t-t: test mode (silent)\n"
HELP="${HELP}\t\t\t-h: print help\n"

function checkSettings(){
   echo "settings check:"
   printf "\tSETTINGS: %s\n" "$SETTINGS"
   printf "\tDATADIR: %s\n" "$DATADIR"
   printf "\tFDADIR: %s\n" "$FDADIR"
   printf "\tFDADATADIR: %s\n" "$FDADATADIR"
   printf "\tSUFFIX_LEN: %s\n" "$SUFFIX_LEN"
   printf "\tPRODS_PER_JSON: %s\n" "$PRODS_PER_JSON"
   printf "\tFB_WRITES_PER_DAY: %s\n" "$FB_WRITES_PER_DAY"
   printf "\tTESTDIR: %s\n" "$TESTDIR"
   printf "\tTESTSETTINGSDIR: %s\n" "$TESTSETTINGSDIR"
   printf "\tTEST_POPDB_SETTINGS: %s\n" "$TEST_POPDB_SETTINGS"
   printf "\tTEST_UPLOADTODB_SETTINGS: %s\n" "$TEST_UPLOADTODB_SETTINGS"
   printf "\tSPLIT_PREFIX: %s\n" "$SPLIT_PREFIX"
   printf "\tOUTFILE_END: %s\n" "$OUTFILE_END"
   printf "\tUPLOAD_SLEEP: %s\n" "$UPLOAD_SLEEP"
   printf "\tLOGDIR: %s\n" "$LOGDIR"
   printf "\tUPLOADLOGDIR: %s\n" "$UPLOADLOGDIR"
   printf "\tSERVER_POPULATED: %s\n" "$SERVER_POPULATED"
   printf "\tDONE_UPLOADING: %s\n" "$DONE_UPLOADING"
   printf "\tUSAGE:\n"
   printf "$USAGE"
   printf "\tHELP:\n"
   printf "$HELP"
}

if [ $# -ne 0 ] ; then
   if [ "$1" == "-s" ] || [ "$1" == "-ns" ] ; then
      checkSettings
      exit 0
   fi
fi


function removePreviousUploads(){
   # if the $LASTUPLOAD file exists, this function will get the last uploaded
   # file and ensure that it and every file below it is deleted before uploading
   # although the function deletes files as it uploads, this ensures that the
   # files haven't shown up again (i.e., through testing).
   if [[ "$silent" == "false" ]] ; then
      echo "checking for previously uploaded files to remove..."
   fi
   upper=$(echo $LASTUPLOAD | grep -oP "[0-9]{$SUFFIX_LEN}(?=.json)")
   for num in $(seq -w 0000 $upper); do
      file="$FDADATADIR$SPLIT_PREFIX$num$OUTFILE_END"
      if [ -e $file ] ; then
         rm $file
      fi
   done
}

function readyNextUpdate(){
   if [[ "$silent" == "false" ]] ; then
      echo "preparing next upload..."
   fi
   # moves the next update into the upload folder
   update_arr=($FDADATADIR*/)
   if [ ${#update_arr[@]} -gt 0 ] ; then
      mv ${update_arr[0]}*.* $FDADATADIR
      rm -r ${update_arr[0]}
   fi
}

function uploadFiles(){
   # uploads a json every $UPLOAD_SLEEP seconds
   success=true
   lastupload=""
   failures=0
   lastfiles=false
   max_file_uploads=$((FB_WRITES_PER_DAY / PRODS_PER_JSON))

   shopt -s nullglob

   alljsons=($FDADATADIR$SPLIT_PREFIX*$OUTFILE_END)
   numjsons=${#alljsons[@]}

   if [[ $numjsons -lt $max_file_uploads ]] ; then
      lastfiles=true
      $max_file_uploads=$numjsons
   fi

   i=0

   while [[ $i -lt $max_file_uploads ]] ; 
   do
      file=${alljsons[$i]}
      if [ $success == "true" ] ; then
         num=$(echo $file | grep -oP "[0-9]{$SUFFIX_LEN}(?=.json)")
         logfile="${UPLOADLOGDIR}${num}.log"

         curl --header "Content-Type: application/json"\
            --request POST --data @$file http://localhost:8080/products\
            &> $logfile

         sed -i 's//\n/g' $logfile

         result=$(cat $logfile | grep -o 'successful')

         if [[ $result == "successful" ]] ; then
            if [[ "$silent" == "false" ]] ; then
               echo "$file was successfully uploaded"
            fi
            lastupload=$file
            rm $file
         else
            if [[ "$silent" == "false" ]] ; then
               echo "$file upload was unsuccessful"
            fi
            if [[ $file == ${@:-1} ]] ; then
               success=false
            elif [[ $failures -gt 2 ]] ; then
               if [[ "$silent" == "false" ]] ; then
                  echo "$failures this session.  Aborting rest."
               fi
               #this ensures we don't keep looping forever on the bad files 
               #at the end
               success=false
            else
               failures=$((failures+1))
               if [[ "$silent" == "false" ]] ; then
                  echo "failures this session: $failures"
               fi
               leftjsons=($FDADATADIR$SPLIT_PREFIX*$OUTFILE_END)
               lastjson=${leftjsons[-1]}
               lastnum=$(echo $lastjson | grep -oP "[0-9]{$SUFFIX_LEN}(?=.json)")
               lastnum=$((lastnum + 1))
               newlast="$FDADATADIR$SPLIT_PREFIX$lastnum$OUTFILE_END"
               mv $file $newlast
               if [[ "$silent" == "false" ]] ; then
                  echo "moving $file to $newlast"
               fi

               if [[ "$lastfiles" == "false" ]] ; then
                  max_file_uploads=$((max_file_uploads + 1))
                  if [[ $numjsons -lt $max_file_uploads ]] ; then
                     lastfiles=true
                  fi
               fi
            fi
         fi
         sleep $UPLOAD_SLEEP
         i=$((i+1))
      fi
   done
   if [ -n "$lastupload" ] ; then
      # note: you can replace sed's delimiter
      # here I'm using @ instead of / because of the forward slashes in 
      # $lastupload, which contains a path and filename
      sed -i "s@^LASTUPLOAD:.*@LASTUPLOAD:$lastupload@g" $SETTINGS
   fi
   if [[ "$lastfiles" = "true" ]] ; then
      resolveLastUpload $success
   fi
}

function resolveLastUpload(){
   # sets up the next queued files for uploading
   if [ $1 == "true" ] ; then
      echo "last file batch uploaded, resolving"
      if [ $SERVER_POPULATED == "false" ] ; then
         sed -i "s/^SERVER_POPULATED:.*/SERVER_POPULATED:true/g" $SETTINGS
         if [ -z $FDADATADIR*/ ] ; then
            if [ $DONE_UPLOADING == "false" ] ; then
               sed -i "s/^DONE_UPLOADING:.*/DONE_UPLOADING:true/g" $SETTINGS
            fi
         else
            readyNextUpdate
         fi
      elif [ -z $FDADATADIR*/ ] ; then
         if [ $DONE_UPLOADING == "false" ] ; then
            sed -i "s/^DONE_UPLOADING:.*/DONE_UPLOADING:true/g" $SETTINGS
         fi
      else
         readyNextUpdate
      fi
   else
      echo "last file upload issue"
      exit 1
   fi
}

if [ $# -ne 0 ] ; then
   if [ "$1" == "-b" ] ; then
      silent=true
   elif [ "$1" == "-h" ] ; then
      printf "$HELP"
      exit 0
   else
      printf "USAGE"
      exit 1
   fi
fi

if [ "$DONE_UPLOADING" != "true" ] ; then
   if [ "$silent" == "false" ] ; then
      echo "This will upload to the server: only one person should be doing it per day."

      read -p 'Are you sure you want to go forward? (y/n): ' response

      if [ "$response" != "y" ] && [ "$response" != "Y" ] ; then
         printf "You entered %s\n" "$response"
         echo "goodbye!"
         exit 0
      fi
   fi

   if [ -n $LASTUPLOAD ] ; then
      removePreviousUploads
   fi
   uploadFiles 
else
   if [[ "$silent" == "false" ]] ; then
      echo "nothing to upload."
   fi
   exit 0
fi
   
