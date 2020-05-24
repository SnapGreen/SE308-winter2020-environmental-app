#!/bin/bash
SETTINGS="/home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/settings.txt"
DATADIR=$(grep -oP '(?<=^DATADIR:).*' $SETTINGS)
NEWDATADIR="$1"
EPADATASOURCE="$2"
NEWDATAPATH="${NEWDATADIR}${EPADATASOURCE}"
AWKDIR=$(grep -oP '(?<=^AWKDIR:).*' $SETTINGS)
TMPDIR=$(grep -oP '(?<=^TMPDIR:).*' $SETTINGS)
TMPFILE_END=$(grep -oP '(?<=^TMPFILE_END:).*' $SETTINGS)
SAFER_PREFIX=$(echo $EPADATASOURCE | grep -oP ".*(?=.xls)")
SAFERCSVROUGH_TMP="${TMPDIR}${SAFER_PREFIX}_roughcsv${TMPFILE_END}"
SAFERCSVPREPPED_TMP="${TMPDIR}${SAFER_PREFIX}_preppedcsv${TMPFILE_END}"
SAFERSPLIT_PREFIX="${TMPDIR}${SAFER_PREFIX}"
SAFEROLD_TMP="${TMPDIR}${SAFER_PREFIX}00"
SAFERNEW_TMP="${TMPDIR}${SAFER_PREFIX}01"
SAFEROLDTRIM_TMP="${TMPDIR}${SAFER_PREFIX}_oldtrim${TMPFILE_END}"
SAFERNEWTRIM_TMP="${TMPDIR}${SAFER_PREFIX}_newtrim${TMPFILE_END}"
SAFERJOINED_TMP="${TMPDIR}${SAFER_PREFIX}_joined${TMPFILE_END}"
AWK_OLDEPA_TRIM="${AWKDIR}epaoldtrim.awk"
AWK_NEWEPA_TRIM="${AWKDIR}epanewtrim.awk"
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
   printf "\tAWKDIR: %s\n" "$AWKDIR"
   printf "\tTMPDIR: %s\n" "$TMPDIR"
   printf "\tTMPFILE_END: %s\n" "$TMPFILE_END"
   printf "\tSAFER_PREFIX: %s\n" "$SAFER_PREFIX"
   printf "\tSAFERCSVROUGH_TMP: %s\n" "$SAFERCSVROUGH_TMP"
   printf "\tSAFERCSVPREPPED_TMP: %s\n" "$SAFERCSVPREPPED_TMP"
   printf "\tSAFERSPLIT_PREFIX: %s\n" "$SAFERSPLIT_PREFIX"
   printf "\tSAFEROLD_TMP: %s\n" "$SAFEROLD_TMP"
   printf "\tSAFERNEW_TMP: %s\n" "$SAFERNEW_TMP"
   printf "\tSAFEROLDTRIM_TMP: %s\n" "$SAFEROLDTRIM_TMP"
   printf "\tSAFERNEWTRIM_TMP: %s\n" "$SAFERNEWTRIM_TMP"
   printf "\tSAFERJOINED_TMP: %s\n" "$SAFERJOINED_TMP"
   printf "\tAWK_OLDEPA_TRIM: %s\n" "$AWK_OLDEPA_TRIM"
   printf "\tAWK_NEWEPA_TRIM: %s\n" "$AWK_NEWEPA_TRIM"
   printf "\tUSAGE:\n"
   printf "$USAGE"
   printf "\tHELP:\n"
   printf "$HELP"
}

function convertToCsv(){
   #https://www.unix.com/shell-programming-and-scripting/156328-how-convert-xls-file-csv.html
   printf "converting to csv...\n"
   xls2csv "$1" > "$2"

   #printf "prepping data...\n"
   #dos2unix $2

   if [ $debug == "off" ] ; then
      rm $1
   fi
}

function prepForSplit(){
   printf "clearing control characters...\n"
   sed -i 's/[[:cntrl:]]//g' "$1"

   printf "converting to lower case...\n"
   cat "$1" | tr A-Z a-z > "$2"

   printf "removing header lines...\n"
   sed -i '/^"list call".*/d' "$2"

   if [ $debug == "off" ] ; then
      rm $1
   fi
}

function splitOldAndNew(){
   printf "splitting old and new items...\n"
   csplit -f "$SAFERSPLIT_PREFIX" "$1" '/^"cas.*/'
}

function processNewData(){
   printf "processing new data...\n"

   #this removes the header line from the new split file
   echo "$(tail -n +2 "$SAFERNEW_TMP")" > "$SAFERNEW_TMP"

   printf "removing redundant data...\n"
   #this file is mainly a log of items that have been added
   #this removes those entries and leaves the ones that have/will be deleted
   sed -i '/"gr[ea]y \[square\]"/!d' "$SAFERNEW_TMP"

   #this will convert the 'grey/gray [square]' to a score of -2
   sed -i 's/gr[ea]y \[square\]/-2/g' "$SAFERNEW_TMP"

   #there is a column in the update portion that sometimes goes unused; in order
   #for awk to process the file correctly, "" needs to be added between ,,
   sed -i 's/,,/,"",/g' "$SAFERNEW_TMP"
}


function extractRelevantOldData(){
   printf "extracting relevant old data...\n"
   > $3
   #created a reduced file consisting only of the parts we need
   awk -f $1 $2 >> $3

   if [ $debug == "off" ] ; then
      rm $2
   fi
}

function extractRelevantNewData(){
   printf "extracting relevant new data...\n"
   > $3
   #created a reduced file consisting only of the parts we need
   awk -f $1 $2 >> $3

   #for whatever reason, awk is leaving an extra "
   #running short on time for the quarter, this works
   #sed -i 's/|"/|/g' "$3"

   if [ $debug == "off" ] ; then
      rm $2
   fi
}

function joinOldAndNew(){
   printf "rejoining old and new items...\n"
   cat $1 $2 > $3
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
   
   convertToCsv "$NEWDATAPATH" "$SAFERCSVROUGH_TMP"
   prepForSplit "$SAFERCSVROUGH_TMP" "$SAFERCSVPREPPED_TMP"
   splitOldAndNew "$SAFERCSVPREPPED_TMP"
   processNewData "$SAFERNEW_TMP"
   extractRelevantOldData "$AWK_OLDEPA_TRIM" "$SAFEROLD_TMP" "$SAFEROLDTRIM_TMP"
   extractRelevantNewData "$AWK_OLDEPA_TRIM" "$SAFERNEW_TMP" "$SAFERNEWTRIM_TMP"
   joinOldAndNew "$SAFEROLDTRIM_TMP" "$SAFERNEWTRIM_TMP" "$SAFERJOINED_TMP"

fi
