#!/bin/bash
SETTINGS="files/settings.txt"
ZIPFILE="$1"
RAWDATADIR=$(grep -oP '(?<=^RAWDATADIR:).*' $SETTINGS)
FDADIR=$(grep -oP '(?<=^FDADIR:).*' $SETTINGS)
FDADATASOURCE=$(grep -oP '(?<=^FDADATASOURCE:).*' $SETTINGS)
FDAUPDATESOURCE=$(grep -oP '(?<=^FDAUPDATESOURCE:).*' $SETTINGS)
RAWDATADEST="${RAWDATADIR}${FDADIR}"
USAGE="Usage: ./distributeFiles.sh <zipfile> [OPTION] (use option -h for help)\n"
HELP="\t-b: bypass debug mode (don't keep temp files)\n"
HELP="${HELP}\t-s: output settings only\n"
HELP="${HELP}\t-h: print help\n"

debug=true
fin=false

function checkSettings(){
   echo "settings check:"
   printf "\tSETTINGS: %s\n" $SETTINGS
   printf "\tZIPFILE: %s\n" $ZIPFILE
   printf "\tRAWDATADIR: %s\n" $RAWDATADIR
   printf "\tFDADIR: %s\n" $FDADIR
   printf "\tFDADATASOURCE: %s\n" $FDADATASOURCE
   printf "\tFDAUPDATESOURCE: %s\n" $FDAUPDATESOURCE
   printf "\tRAWDATADEST: %s\n" $RAWDATADEST
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
   printf "moving files to dataraw/FDA...\n"
   mv "$1" "$3"
   mv "$2" "$3"
}


if [[ $# -gt 1 ]] ; then
   if [ "$2" == "-b" ] ; then
      debug=false
   elif [ "$2" == "-s" ] ; then
      checkSettings
      fin=true
   elif [ "$2" == "-h" ] ; then
      printf $HELP
      fin=true
   else
      printf "$USAGE"
      fin=true
   fi
elif [[ $# -lt 1 ]] ; then
   printf "$USAGE"
   fin=true
fi

if [ "$fin" == "false" ] ; then
   if [ "$debug" == "true" ] ; then
      checkSettings
   fi

   expandArchive $ZIPFILE

   if [ "$debug" == "false" ] ; then
      deleteUnneededFiles $ZIPFILE
   fi

   moveToRawFDADir $FDADATASOURCE $FDAUPDATESOURCE $RAWDATADEST
fi

