#!/bin/bash

silent=false

THISPATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd)"
SETTINGS="${THISPATH}/settings.txt"

if [ $# -eq 1 ] ; then
   if [ "$1" == "-t" ] || [ "$1" == "-n" ] || [ "$1" == "-ns" ] ; then
      silent=true
      if [ "$1" == "-n" ] || [ "$1" == "-ns" ] ; then
         SETTINGS="${THISPATH}/settings_npm.txt"
      fi
   fi
fi

SERVER_POPULATED=$(grep -oP '(?<=^SERVER_POPULATED:).*' $SETTINGS)
DONE_UPLOADING=$(grep -oP '(?<=^DONE_UPLOADING:).*' $SETTINGS)
LASTUPLOAD=$(grep -oP '(?<=^LASTUPLOAD:).*' $SETTINGS)
USAGE="\t\tUsage: ./populateDB.sh [OPTION] (use option -h for help)\n"
HELP="\t\tThis script will download the latest FDA database\n"
HELP="${HELP}\t\tand adjust the settings so that updates are stored,\n"
HELP="${HELP}\t\tthen applied after the databse is fully populated.\n"
HELP="${HELP}${USAGE}"
HELP="${HELP}\t\t\t-n: test relative to repo root\n"
HELP="${HELP}\t\t\t-ns: output settings relative to repo root only\n"
HELP="${HELP}\t\t\t-s: output settings only\n"
HELP="${HELP}\t\t\t-t: test mode (silent)\n"
HELP="${HELP}\t\t\t-h: print help\n"

function checkSettings(){
   echo "settings check:"
   printf "\tSETTINGS: %s\n" $SETTINGS
   printf "\tSERVER_POPULATED: %s\n" $SERVER_POPULATED
   #printf "\tLASTUPLOAD: %s\n" $LASTUPLOAD
   printf "\tUSAGE:\n"
   printf "$USAGE"
   printf "\tHELP:\n"
   printf "$HELP"
}

if [ $# -eq 1 ] ; then
   if [ $1 == "-s" ] || [ "$1" == "-ns" ] ; then
      checkSettings
      exit 0
   fi
fi

if [ $# -eq 0 ] ; then
   if [[ "$silent" == "false" ]] ; then
      echo "modifying settings for initial db population..."
   fi
   sed -i 's/^SERVER_POPULATED:.*/SERVER_POPULATED:false/g' $SETTINGS
   sed -i 's/^DONE_UPLOADING:.*/DONE_UPLOADING:false/g' $SETTINGS
   sed -i 's/^LASTUPLOAD:.*/LASTUPLOAD:/g' $SETTINGS
   ./getFDAUpdate -f
elif [ $# -eq 1 ] ; then
   if [ $1 == "-h" ] ; then
      printf "$HELP"
      exit 0
   else
      printf "$USAGE"
      exit 1
   fi
else
   printf "$USAGE"
   exit 1
fi
