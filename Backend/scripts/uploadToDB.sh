#!/bin/bash
fin=false
prompt=true

function checkSettingsNPM(){
   SETTINGS_NPM="./Backend/scripts/settings_npm.txt"
   DATADIR=$(grep -oP "(?<=^DATADIR:).*" $SETTINGS_NPM)
   FDADIR=$(grep -oP "(?<=^FDADIR:).*" $SETTINGS_NPM)
   FDADATADIR="${DATADIR}${FDADIR}"
   SUFFIX_LEN=$(grep -oP "(?<=^SUFFIX_LEN:).*" $SETTINGS_NPM)
   PRODS_PER_JSON=$(grep -oP "(?<=^PRODS_PER_JSON:).*" $SETTINGS_NPM)
   FB_WRITES_PER_DAY=$(grep -oP "(?<=^FB_WRITES_PER_DAY:).*" $SETTINGS_NPM)
   TESTDIR=$(grep -oP "(?<=TESTDIR).*" $SETTINGS_NPM)
   TESTSETTINGSDIR=$"${TESTDIR}settings/"
   TEST_POPDB_SETTINGS="${TESTSETTINGSDIR}populateDB_settings.txt"
   TEST_UPLOADTODB_SETTINGS="${TESTSETTINGSDIR}uploadToDB_settings.txt"
   SPLIT_PREFIX=$(grep -oP "(?<=^SPLIT_PREFIX:).*" $SETTINGS_NPM)
   OUTFILE_END=$(grep -oP "(?<=^OUTFILE_END:).*" $SETTINGS_NPM)
   UPLOAD_SLEEP=$(grep -oP "(?<=^UPLOAD_SLEEP:).*" $SETTINGS_NPM)
   LOGDIR=$(grep -oP "(?<=^LOGDIR:).*" $SETTINGS_NPM)
   UPLOADLOGDIR="${LOGDIR}uploads/"
   SERVER_POPULATED=$(grep -oP "(?<=^SERVER_POPULATED:).*" $SETTINGS_NPM)
   DONE_UPLOADING=$(grep -oP "(?<=^DONE_UPLOADING:).*" $SETTINGS_NPM)
   USAGE="\t\tUsage: ./uploadToDB.sh [OPTIONS] (-h for help)\n"
   HELP="${USAGE}\t\tOPTIONS:\n"
   HELP="${HELP}\t\t\t-b: upload without prompt\n"
   HELP="${HELP}\t\t\t-s: print settings only\n"
   HELP="${HELP}\t\t\t-h: print help\n"

   echo "settings check:"
   printf "\tSETTINGS: %s\n" "$SETTINGS_NPM"
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
   if [ "$1" == "-t" ] ; then
      checkSettingsNPM
      exit 0
   fi
fi

SETTINGS="/home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/settings.txt"
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
HELP="${HELP}\t\t\t-s: print settings only\n"
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
   #printf "\tLASTUPLOAD: %s\n" "$LASTUPLOAD"
   printf "\tSERVER_POPULATED: %s\n" "$SERVER_POPULATED"
   printf "\tDONE_UPLOADING: %s\n" "$DONE_UPLOADING"
   printf "\tUSAGE:\n"
   printf "$USAGE"
   printf "\tHELP:\n"
   printf "$HELP"
}

function removePreviousUploads(){
   # if the $LASTUPLOAD file exists, this function will get the last uploaded
   # file and ensure that it and every file below it is deleted before uploading
   # although the function deletes files as it uploads, this ensures that the
   # files haven't shown up again (i.e., through testing).
   upper=$(echo $LASTUPLOAD | grep -oP "[0-9]{$SUFFIX_LEN}(?=.json)")
   for num in $(seq -w 0000 $upper); do
      file="$FDADATADIR$SPLIT_PREFIX$num$OUTFILE_END"
      if [ -e $file ] ; then
         rm $file
      fi
   done
}

function readyNextUpdate(){
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
            echo "$file was successfully uploaded"
            lastupload=$file
            rm $file
         else
            echo "$file upload was unsuccessful"
            if [[ $file == ${@:-1} ]] ; then
               success=false
            elif [[ $failures -gt 2 ]] ; then
               echo "$failures this session.  Aborting rest."
               #this ensures we don't keep looping forever on the bad files 
               #at the end
               success=false
            else
               failures=$((failures+1))
               echo "failures this session: $failures"
               leftjsons=($FDADATADIR$SPLIT_PREFIX*$OUTFILE_END)
               lastjson=${leftjsons[-1]}
               lastnum=$(echo $lastjson | grep -oP "[0-9]{$SUFFIX_LEN}(?=.json)")
               lastnum=$((lastnum + 1))
               newlast="$FDADATADIR$SPLIT_PREFIX$lastnum$OUTFILE_END"
               mv $file $newlast
               echo "moving $file to $newlast"

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
      # this line updates the relevant line for the settings tests
      sed -i "s@LASTUPLOAD: .*@LASTUPLOAD: $lastupload@g" $TEST_UPLOADTODB_SETTINGS
      sed -i "s@LASTUPLOAD: .*@LASTUPLOAD: $lastupload@g" $TEST_POPDB_SETTINGS
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
   fi
}

if [ $# -ne 0 ] ; then
   if [ "$1" == "-s" ] ; then
      checkSettings
      fin=true
      prompt=false
   elif [ "$1" == "-t" ] ; then
      checkSettingsNPM
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

if [ "$DONE_UPLOADING" != "true" ] ; then
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
      if [ -n $LASTUPLOAD ] ; then
         removePreviousUploads
      fi

      uploadFiles 

   fi
else
   echo "nothing to upload."
fi
   
