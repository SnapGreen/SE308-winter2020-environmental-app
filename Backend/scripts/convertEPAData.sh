#!/bin/bash
SETTINGS="/home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/settings.txt"
DATADIR=$(grep -oP '(?<=^DATADIR:).*' $SETTINGS)
NEWDATADIR=$1
EPADATASOURCE=$2
NEWDATAPATH="${NEWDATADIR}${EPADATASOURCE}"
TEMPDIR=$(grep -oP '(?<=^TEMPDIR:).*' $SETTINGS)
USAGE="\t\tUsage: ./convertEPAData.sh [OPTION] (use option -h for help)\n"
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
   printf "\tDATADIR: %s\n" "$DATADIR"
   printf "\tNEWDATADIR: %s\n" "$NEWDATADIR"
   printf "\tEPADATASOURCE: %s\n" "$EPADATASOURCE"
   printf "\tNEWDATAPATH: %s\n" "$NEWDATAPATH"
   printf "\tUSAGE:\n"
   printf "$USAGE"
   printf "\tHELP:\n"
   printf "$HELP"
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
   else
      printf "$USAGE"
      fin=true
   fi
fi

if [ "$fin" == "false" ] ; then
   if [ "$silent" == "false" ] ; then
      checkSettings
   fi

fi
