#!/bin/bash

THISPATH=$(pwd)
SETTINGS="${THISPATH}/settings.txt"

if [[ $# -gt 2 ]] ; then
   if [ -n $3 ] ; then
      if [ "$3" == "-t" ] || [ "$3" == "-n" ] ; then
         SETTINGS="${THISPATH}/settings_npm.txt"
      fi
   fi
fi

debug=true
fin=false

FILENAME="$1"
FULLPATH="${2}/${FILENAME}"
LOGDIR=$(grep -oP '(?<=^LOGDIR:).*' $SETTINGS)
CURRENTLATEST=$(grep -oP '(?<=^CURRENTLATEST:).*' $SETTINGS)
DONE_UPLOADING=$(grep -oP '(?<=^DONE_UPLOADING:).*' $SETTINGS)
DOWNLOADLOGDIR="${LOGDIR}downloads/"
USAGE="\t\tUsage: ./downloadData.sh <filename> <url> [OPTION] (-h for help)\n"
HELP="${USAGE}\t\t\t-b: bypass debug (will skip settings check)\n"
HELP="${HELP}\t\t\t-s: output settings only\n"
HELP="${HELP}\t\t\t-h: print help\n"

function checkSettings(){
   echo "settings check:"
   printf "\tSETTINGS: %s\n" "$SETTINGS"
   printf "\tFILENAME: %s\n" "$FILENAME"
   printf "\tFULLPATH: %s\n" "$FULLPATH"
   printf "\tLOGDIR: %s\n" "$LOGDIR"
   printf "\tCURRENTLATEST: %s\n" "$CURRENTLATEST"
   printf "\tDONE_UPLOADING: %s\n" $DONE_UPLOADING
   printf "\tDOWNLOADLOGDIR: %s\n" "$DOWNLOADLOGDIR"
   printf "\tUSAGE:\n"
   printf "$USAGE"
   printf "\tHELP:\n"
   printf "$HELP"
}

if [[ $# -gt 2 ]] ; then
   if [ -n $3 ] ; then
      if [ "$3" == "-t" ] ; then
         checkSettings
         exit 0
      fi
   fi
fi

function getData(){
   printf "dowloading from %s\n" "$1"
   timestamp=$(date +%s)
   logfile="${DOWNLOADLOGDIR}${timestamp}.log"
   curl -vs -O --stderr $logfile $FULLPATH
   if [ $? -eq 0 ] ; then
      if [ "$DONE_UPLOADING" == "true" ] ; then
         sed -i "s/^DONE_UPLOADING:.*/DONE_UPLOADING:false/g" $SETTINGS
      fi
      sed -i "s/^LASTLATEST:.*/LASTLATEST:$CURRENTLATEST/g" "$SETTINGS"
   fi
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

