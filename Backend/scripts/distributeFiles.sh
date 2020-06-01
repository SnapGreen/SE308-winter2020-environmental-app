#!/bin/bash
SETTINGS="/home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/settings.txt"
ZIPFILE="$1"
DATADIR=$(grep -oP '(?<=^DATADIR:).*' $SETTINGS)
RAWDATADIR=$(grep -oP '(?<=^RAWDATADIR:).*' $SETTINGS)
FDADIR=$(grep -oP '(?<=^FDADIR:).*' $SETTINGS)
FDADATASOURCE=$(grep -oP '(?<=^FDADATASOURCE:).*' $SETTINGS)
FDAUPDATESOURCE=$(grep -oP '(?<=^FDAUPDATESOURCE:).*' $SETTINGS)
SERVER_POPULATED=$(grep -oP '(?<=^SERVER_POPULATED:).*' $SETTINGS)
DONE_UPLOADING=$(grep -oP '(?<=^SERVER_POPULATED:).*' $SETTINGS)
LASTLATEST=$(grep -oP '(?<=^LASTLATEST:).*' $SETTINGS)
RAWDATADEST="${RAWDATADIR}${FDADIR}"
DATADEST="${DATADIR}${FDADIR}"
IFS=',' read -r -a lastarray <<< "$LASTLATEST"
RAWDATAUPDATEDEST="${RAWDATADEST}${lastarray[1]}/"
DATAUPDATEDEST="${DATADEST}${lastarray[1]}/"
HELP="\tThis script distributes the contents of the provided FDA zipfile to\n"
HELP="${HELP}\tthe proper directories.  If there are no uploads left, it\n"
HELP="${HELP}\tputs them in the upload directory.  Otherwise, it creates\n"
HELP="${HELP}\ta new directory in both data/ and dataraw/ for the update\n"
USAGE="\t\tUsage: ./distributeFiles.sh <zipfile> [OPTION] (use option -h for help)\n"
HELP="${HELP}${USAGE}"
HELP="${HELP}\t\t\t-b: bypass debug mode (don't keep temp files)\n"
HELP="${HELP}\t\t\t-s: output settings only\n"
HELP="${HELP}\t\t\t-h: print help\n"

debug=true
fin=false

function checkSettings(){
   echo "settings check:"
   printf "\tSETTINGS: %s\n" "$SETTINGS"
   printf "\tZIPFILE: %s\n" "$ZIPFILE"
   printf "\tDATADIR: %s\n" "$DATADIR"
   printf "\tRAWDATADIR: %s\n" "$RAWDATADIR"
   printf "\tFDADIR: %s\n" "$FDADIR"
   printf "\tFDADATASOURCE: %s\n" "$FDADATASOURCE"
   printf "\tFDAUPDATESOURCE: %s\n" "$FDAUPDATESOURCE"
   printf "\tLASTLATEST: %s\n" "$LASTLATEST"
   printf "\tRAWDATADEST: %s\n" "$RAWDATADEST"
   printf "\tDATADEST: %s\n" "$DATADEST"
   printf "\tRAWDATAUPDATEDEST: %s\n" "$RAWDATAUPDATEDEST"
   printf "\tDATAUPDATEDEST: %s\n" "$DATAUPDATEDEST"
   printf "\tUSAGE:\n"
   printf "$USAGE"
   printf "\tHELP:\n"
   printf "$HELP"
}

function checkSettingsNPM(){
   SETTINGS_NPM="./Backend/scripts/settings_npm.txt"
   ZIPFILE="$1"
   DATADIR=$(grep -oP '(?<=^DATADIR:).*' $SETTINGS_NPM)
   RAWDATADIR=$(grep -oP '(?<=^RAWDATADIR:).*' $SETTINGS_NPM)
   FDADIR=$(grep -oP '(?<=^FDADIR:).*' $SETTINGS_NPM)
   FDADATASOURCE=$(grep -oP '(?<=^FDADATASOURCE:).*' $SETTINGS_NPM)
   FDAUPDATESOURCE=$(grep -oP '(?<=^FDAUPDATESOURCE:).*' $SETTINGS_NPM)
   SERVER_POPULATED=$(grep -oP '(?<=^SERVER_POPULATED:).*' $SETTINGS_NPM)
   DONE_UPLOADING=$(grep -oP '(?<=^SERVER_POPULATED:).*' $SETTINGS_NPM)
   LASTLATEST=$(grep -oP '(?<=^LASTLATEST:).*' $SETTINGS_NPM)
   RAWDATADEST="${RAWDATADIR}${FDADIR}"
   DATADEST="${DATADIR}${FDADIR}"
   IFS=',' read -r -a lastarray <<< "$LASTLATEST"
   RAWDATAUPDATEDEST="${RAWDATADEST}${lastarray[1]}/"
   DATAUPDATEDEST="${DATADEST}${lastarray[1]}/"

   echo "settings check:"
   printf "\tSETTINGS: %s\n" "$SETTINGS_NPM"
   printf "\tZIPFILE: %s\n" "$ZIPFILE"
   printf "\tDATADIR: %s\n" "$DATADIR"
   printf "\tRAWDATADIR: %s\n" "$RAWDATADIR"
   printf "\tFDADIR: %s\n" "$FDADIR"
   printf "\tFDADATASOURCE: %s\n" "$FDADATASOURCE"
   printf "\tFDAUPDATESOURCE: %s\n" "$FDAUPDATESOURCE"
   printf "\tLASTLATEST: %s\n" "$LASTLATEST"
   printf "\tRAWDATADEST: %s\n" "$RAWDATADEST"
   printf "\tDATADEST: %s\n" "$DATADEST"
   printf "\tRAWDATAUPDATEDEST: %s\n" "$RAWDATAUPDATEDEST"
   printf "\tDATAUPDATEDEST: %s\n" "$DATAUPDATEDEST"
   printf "\tUSAGE:\n"
   printf "$USAGE"
   printf "\tHELP:\n"
   printf "$HELP"
}

function expandArchive(){
   printf "unzipping %s...\n" "$1"
   unzip "$1"
}

function deleteUnneededFiles(){
   printf "removing unneeded files...\n"
   rm food.csv
   rm food_attribute.csv
   rm food_nutrient.csv
   rm all_downloaded_table_record_counts.csv
   rm Download*.pdf
   rm "$1"
}

function moveToRawFDADir(){
   printf "moving files to $3...\n"
   mv "$1" "$3"
   mv "$2" "$3"
}


if [[ $# -gt 1 ]] ; then
   if [ "$2" == "-b" ] ; then
      debug=false
   elif [ "$2" == "-s" ] ; then
      checkSettings
      fin=true
   elif [ "$2" == "-t" ] ; then
      checkSettingsNPM
      fin=true
   elif [ "$2" == "-h" ] ; then
      printf "$HELP"
      fin=true
   else
      printf "$USAGE"
      fin=true
   fi
elif [[ $# -lt 1 ]] ; then
   printf "$USAGE"
   fin=true
else
   if [ "$1" == "-h" ] ; then
      printf "$HELP"
      fin=true
   elif [ "$1" == "-s" ] ; then
      checkSettings
      fin=true
   fi
fi

if [ "$fin" == "false" ] ; then
   if [ "$debug" == "true" ] ; then
      checkSettings
   fi

   expandArchive $ZIPFILE

   if [ "$debug" == "false" ] ; then
      deleteUnneededFiles $ZIPFILE
   fi

   if [ $SERVER_POPULATED == "true" ] ; then
      moveToRawFDADir $FDADATASOURCE $FDAUPDATESOURCE $RAWDATADEST
   elif [ "$DONE_UPLOADING" == "true" ] ; then
      moveToRawFDADir $FDADATASOURCE $FDAUPDATESOURCE $RAWDATADEST
   else
      mkdir $RAWDATAUPDATEDEST
      mkdir $DATAUPDATEDEST
      moveToRawFDADir $FDADATASOURCE $FDAUPDATESOURCE $RAWDATAUPDATEDEST
   fi
fi

