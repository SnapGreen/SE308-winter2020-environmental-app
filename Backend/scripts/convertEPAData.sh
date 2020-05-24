#!/bin/bash
SETTINGS="/home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/settings.txt"
DATADIR=$(grep -oP '(?<=^DATADIR:).*' $SETTINGS)
NEWDATADIR="$1"
EPADATASOURCE="$2"
NEWDATAPATH="${NEWDATADIR}${EPADATASOURCE}"
TMPDIR=$(grep -oP '(?<=^TMPDIR:).*' $SETTINGS)
TMPFILE_END=$(grep -oP '(?<=^TMPFILE_END:).*' $SETTINGS)
SAFER_PREFIX=$(echo $EPADATASOURCE | grep -oP ".*(?=.xls)")
SAFERCSV_TMP="${TMPDIR}${SAFER_PREFIX}_csv${TMPFILE_END}"
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
   printf "\tTMPDIR: %s\n" "$TMPDIR"
   printf "\tTMPFILE_END: %s\n" "$TMPFILE_END"
   printf "\tSAFER_PREFIX: %s\n" "$SAFER_PREFIX"
   printf "\tSAFERCSV_TMP: %s\n" "$SAFERCSV_TMP"
   printf "\tUSAGE:\n"
   printf "$USAGE"
   printf "\tHELP:\n"
   printf "$HELP"
}

function prepData(){
   echo "prepping data"
   dos2unix $1
}

function convertToCsv(){
   #https://www.unix.com/shell-programming-and-scripting/156328-how-convert-xls-file-csv.html
   echo "converting to csv"
   xls2csv "$1" > "$2"
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
   if [[ "$debug" == "true" ]] ; then
      checkSettings
   fi
   
   prepData "$NEWDATAPATH"
   convertToCsv "$NEWDATAPATH" "$SAFERCSV_TMP"

fi
