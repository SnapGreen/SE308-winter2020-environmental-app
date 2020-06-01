#!/bin/bash
SETTINGS="/home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/settings.txt"
SERVER_POPULATED=$(grep -oP '(?<=^SERVER_POPULATED:).*' $SETTINGS)
DONE_UPLOADING=$(grep -oP '(?<=^DONE_UPLOADING:).*' $SETTINGS)
LASTUPLOAD=$(grep -oP '(?<=^LASTUPLOAD:).*' $SETTINGS)
USAGE="\t\tUsage: ./populateDB.sh [OPTION] (use option -h for help)\n"
HELP="\t\tThis script will download the latest FDA database\n"
HELP="${HELP}\t\tand adjust the settings so that updates are stored,\n"
HELP="${HELP}\t\tthen applied after the databse is fully populated.\n"
HELP="${HELP}${USAGE}"
HELP="${HELP}\t\t\t-s: output settings only\n"
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

function checkSettingsNPM(){
   SETTINGS_NPM="./Backend/scripts/settings.txt"
   SERVER_POPULATED=$(grep -oP '(?<=^SERVER_POPULATED:).*' $SETTINGS_NPM)
   DONE_UPLOADING=$(grep -oP '(?<=^DONE_UPLOADING:).*' $SETTINGS_NPM)
   echo "settings check:"
   printf "\tSETTINGS: %s\n" $SETTINGS_NPM
   printf "\tSERVER_POPULATED: %s\n" $SERVER_POPULATED
   printf "\tUSAGE:\n"
   printf "$USAGE"
   printf "\tHELP:\n"
   printf "$HELP"
}

if [ $# -eq 0 ] ; then
   sed -i 's/^SERVER_POPULATED:.*/SERVER_POPULATED:false/g' $SETTINGS

   sed -i 's/^DONE_UPLOADING:.*/DONE_UPLOADING:false/g' $SETTINGS

   sed -i 's/^LASTUPLOAD:.*/LASTUPLOAD:/g' $SETTINGS

   ./getFDAUpdate -f
elif [ $# -eq 1 ] ; then
   if [ $1 == "-h" ] ; then
      printf "$HELP"
   elif [ $1 == "-s" ] ; then
      checkSettings
   elif [ $1 == "-t" ] ; then
      checkSettingsNPM
   else
      printf "$USAGE"
   fi
else
   printf "$USAGE"
fi
