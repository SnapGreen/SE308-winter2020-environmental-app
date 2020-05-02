#!/bin/bash
SETTINGS="files/settings.txt"
FILENAME="$1"
FULLPATH="${2}/${FILENAME}"
LOGDIR=$(grep -oP '(?<=LOGDIR:).*' $SETTINGS)
DOWNLOADLOGDIR="${LOGDIR}downloads/"
USAGE="Usage: ./downloadData.sh <filename> <url> [OPTION] (-h for help)\n"
HELP="${USAGE}\t-b: bypass debug (will skip settings check)\n"
HELP="${HELP}\t-s: output settings only\n"
HELP="${HELP}\t-h: print help\n"

debug=true
fin=false

function checkSettings(){
   printf "SETTINGS: %s\n" $SETTINGS
   printf "FILENAME: %s\n" $FILENAME
   printf "FULLPATH: %s\n" $FULLPATH
   printf "LOGDIR: %s\n" $LOGDIR
   printf "DOWNLOADLOGDIR: %s\n" $DOWNLOADLOGDIR
}

function getData(){
   printf "dowloading from %s\n" "$1"
   timestamp=$(date +%s)
   logfile="${DOWNLOADLOGDIR}${timestamp}.log"
   #curl -v -O $FULLPATH 
   # for logging (needs testing):
   curl -vs -O --stderr $logfile $FULLPATH
}

if [[ $# -gt 2 ]] ; then
   if [ -n $3 ] ; then
      if [ "$3" == "-b" ] ; then
         debug=false
      elif [ "$3" == "-h" ] ; then
         printf "$HELP"
         fin=true
      elif [ "$3" == "-s" ] ; then
         checkSettings
         fin=true
      else
         printf "$USAGE"
         fin=true
      fi
   fi
elif [[ $# -lt 2 ]] ; then
   printf "$USAGE"
   fin=true
fi
   
if [ "$fin" == "false" ] ; then
   if [ "$debug" == "true" ] ; then
      checkSettings
   fi

   getData $FULLPATH

   ./distributeFiles.sh $FILENAME -b
fi

