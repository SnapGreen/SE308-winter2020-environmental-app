#!/bin/bash
SETTINGS="/home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/settings.txt"
EPA_DIR_ADDRESS=$(grep -oP '(?<=^EPA_DIR_ADDRESS:).*' $SETTINGS)
EPADATASOURCE=$(grep -oP '(?<=^EPADATASOURCE:).*' $SETTINGS)
EPA_DATA_URL="${EPA_DIR_ADDRESS}${EPADATASOURCE}"
RAWDATADIR=$(grep -oP '(?<=^RAWDATADIR:).*' $SETTINGS)
EPADIR=$(grep -oP '(?<=^EPADIR:).*' $SETTINGS)
RAWDATADEST="${RAWDATADIR}${EPADIR}"
LASTDATAPATH="${RAWDATADEST}${EPADATASOURCE}"
LOGDIR=$(grep -oP '(?<=^LOGDIR:).*' $SETTINGS)
DOWNLOADLOGDIR="${LOGDIR}downloads/"
USAGE="\t\tUsage: ./getEPAUpdate.sh [OPTION] (use option -h for help)\n"
HELP="${USAGE}\t\t**If no OPTION supplied, debug mode on (temp files remain)\n"
HELP="${HELP}\t\t\t-b: bypass debug mode\n"
HELP="${HELP}\t\t\t-s: output settings only\n"
HELP="${HELP}\t\t\t-h: print help\n"

debug=true
fin=false
silent=false

function checkSettings(){
   echo "settings check:"
   printf "\tSETTINGS: %s\n" $SETTINGS
   printf "\tEPA_DIR_ADDRESS: %s\n" "$EPA_DIR_ADDRESS"
   printf "\tEPADATASOURCE: %s\n" "$EPADATASOURCE"
   printf "\tEPA_DATA_URL: %s\n" "$EPA_DATA_URL"
   printf "\tRAWDATADIR: %s\n" "$RAWDATADIR"
   printf "\tEPADIR: %s\n" "$EPADIR"
   printf "\tRAWDATADEST: %s\n" "$RAWDATADEST"
   printf "\tLASTDATAPATH: %s\n" "$LASTDATAPATH"
   printf "\tLOGDIR: %s\n" "$LOGDIR"
   printf "\tDOWNLOADLOGIR: %s\n" "$DOWNLOADLOGDIR"
   printf "\tUSAGE:\n"
   printf "$USAGE"
   printf "\tHELP:\n"
   printf "$HELP"
}

function checkSettingsNPM(){
   SETTINGS_NPM="./Backend/scripts/settings_npm.txt"
   EPA_DIR_ADDRESS=$(grep -oP '(?<=^EPA_DIR_ADDRESS:).*' $SETTINGS_NPM)
   EPADATASOURCE=$(grep -oP '(?<=^EPADATASOURCE:).*' $SETTINGS_NPM)
   EPA_DATA_URL="${EPA_DIR_ADDRESS}${EPADATASOURCE}"
   RAWDATADIR=$(grep -oP '(?<=^RAWDATADIR:).*' $SETTINGS_NPM)
   EPADIR=$(grep -oP '(?<=^EPADIR:).*' $SETTINGS_NPM)
   RAWDATADEST="${RAWDATADIR}${EPADIR}"
   LASTDATAPATH="${RAWDATADEST}${EPADATASOURCE}"
   LOGDIR=$(grep -oP '(?<=^LOGDIR:).*' $SETTINGS_NPM)
   DOWNLOADLOGDIR="${LOGDIR}downloads/"

   echo "settings check:"
   printf "\tSETTINGS: %s\n" $SETTINGS_NPM
   printf "\tEPA_DIR_ADDRESS: %s\n" "$EPA_DIR_ADDRESS"
   printf "\tEPADATASOURCE: %s\n" "$EPADATASOURCE"
   printf "\tEPA_DATA_URL: %s\n" "$EPA_DATA_URL"
   printf "\tRAWDATADIR: %s\n" "$RAWDATADIR"
   printf "\tEPADIR: %s\n" "$EPADIR"
   printf "\tRAWDATADEST: %s\n" "$RAWDATADEST"
   printf "\tLASTDATAPATH: %s\n" "$LASTDATAPATH"
   printf "\tLOGDIR: %s\n" "$LOGDIR"
   printf "\tDOWNLOADLOGIR: %s\n" "$DOWNLOADLOGDIR"
   printf "\tUSAGE:\n"
   printf "$USAGE"
   printf "\tHELP:\n"
   printf "$HELP"
}

function getData(){
   printf "dowloading from %s\n" "$EPA_DATA_ADDRESS"
   timestamp=$(date +%s)
   logfile="${DOWNLOADLOGDIR}${timestamp}_epa.log"
   curl -vs -O --stderr $logfile "$EPA_DATA_URL"
   if [ $? -eq 0 ] ; then
      echo "download successful"
      checkIfUpdated
   else
      echo "download failed"
      fin=true
   fi
}

function checkIfUpdated(){
   echo "checking for newer version"
   if [ -e $LASTDATAPATH ] ; then
      echo "diffing against current file"
      diff "$LASTDATAPATH" "$EPADATASOURCE"
      exitcode=$?
      if [[ $exitcode == 0 ]] ; then
         echo "no update available"
         fin=true
      else
         echo "update found, replacing old"
         mv $EPADATASOURCE "$RAWDATADEST"
      fi
   else
      echo "new EPA source moved to raw data directory"
      mv $EPADATASOURCE "$RAWDATADEST"
   fi
}


if [[ -n $1 ]] ; then
   if [ "$1" == "-h" ] ; then
      printf "$HELP"
      fin=true
   elif [ "$1" == "-f" ] || [ "$1" == "-b" ] ; then
      debug=false
      silent=true
   elif [ "$1" == "-s" ] ; then
      checkSettings
      fin=true
   elif [ "$1" == "-t" ] ; then
      checkSettingsNPM
      fin=true
   else
      printf "$USAGE"
      fin=true
   fi
fi

if [ "$fin" == "false" ] ; then
   if [ "$silent" == "false" ] ; then
      checkSettings
   fi

   getData

   if [ "$fin" == "false" ] ; then
      ./convertEPAData.sh "$RAWDATADEST" "$EPADATASOURCE"
   fi
fi

