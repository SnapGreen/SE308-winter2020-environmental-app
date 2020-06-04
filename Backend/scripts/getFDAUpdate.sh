#!/bin/bash

debug=true
silent=false

THISPATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd)"
SETTINGS="${THISPATH}/settings.txt"

if [[ -n $1 ]] ; then
   if [ "$1" == "-t" ] || [ "$1" == "-n" ] || [ "$1" == "-ns" ] || [ "$1" == "-f" ] ; then
      silent=true
      if [ "$1" == "-n" ] || [ "$1" == "-ns" ] ; then
         #SETTINGS="${THISPATH}/settings_npm.txt"
         SETTINGS="Backend/scripts/settings_npm.txt"
      fi
   fi
fi

CURRENTLATEST=$(grep -oP '(?<=^CURRENTLATEST:).*' $SETTINGS)
LASTLATEST=$(grep -oP '(?<=^LASTLATEST:).*' $SETTINGS)
SERVER_POPULATED=$(grep -oP '(?<=^SERVER_POPULATED:).*' $SETTINGS)
FDC_DIR_ADDRESS=$(grep -oP '(?<=^FDC_DIR_ADDRESS:).*' $SETTINGS)
TMPDIR=$(grep -oP '(?<=^TMPDIR:).*' $SETTINGS)
TMPFILE_END=$(grep -oP '(?<=^TMPFILE_END:).*' $SETTINGS)
TMPLINKSFILE="${TMPDIR}links${TMPFILE_END}"
TMPFILELIST="${TMPDIR}available_data${TMPFILE_END}"
USAGE="\t\tUsage: ./getFDAUpdate.sh [OPTION] (use option -h for help)\n"
HELP="${USAGE}\t\t**If no OPTION supplied, debug mode on (temp files remain)\n"
HELP="${HELP}\t\t\t-b: bypass debug mode, download only if new\n"
HELP="${HELP}\t\t\t-f: force download\n"
HELP="${HELP}\t\t\t-n: test relative to repo root\n"
HELP="${HELP}\t\t\t-ns: output settings relative to repo root\n"
HELP="${HELP}\t\t\t-s: output settings only\n"
HELP="${HELP}\t\t\t-t: test mode (silent)\n"
HELP="${HELP}\t\t\t-h: print help\n"

function checkSettings(){
   echo "settings check:"
   printf "\tSETTINGS: %s\n" $SETTINGS
   printf "\tCURRENTLATEST: %s\n" $CURRENTLATEST
   printf "\tLASTLATEST: %s\n" $LASTLATEST
   printf "\tSERVER_POPULATED: %s\n" $SERVER_POPULATED
   printf "\tFDC_DIR_ADDRESS: %s\n" $FDC_DIR_ADDRESS
   printf "\tTMPDIR: %s\n" $TMPDIR
   printf "\tTMPFILE_END: %s\n" $TMPFILE_END
   printf "\tTMPLINKSFILE: %s\n" $TMPLINKSFILE
   printf "\tTMPFILELIST: %s\n" $TMPFILELIST
   printf "\tUSAGE:\n"
   printf "$USAGE"
   printf "\tHELP:\n"
   printf "$HELP"
}

if [[ -n $1 ]] ; then
   if [ "$1" == "-s" ] || [ "$1" == "-ns" ]; then
      checkSettings
      exit 0
   fi
fi


function convertMonthToDigits(){
   if [[ "$silent" == "false" ]] ; then
      echo "converting months to digit format..."
   fi
   sed -i 's/-Jan-/01/; t;
           s/-Feb-/02/; t;
           s/-Mar-/03/; t;
           s/-Apr-/04/; t;
           s/-May-/05/; t;
           s/-Jun-/06/; t;
           s/-Jul-/07/; t;
           s/-Aug-/08/; t;
           s/-Sep-/09/; t;
           s/-Oct-/10/; t;
           s/-Nov-/11/; t;
           s/-Dec-/12/;' $1
}

function rearrangeDate(){
   if [[ "$silent" == "false" ]] ; then
      echo "rearranging date..."
   fi
   a='s/,\([0-9]\{2\}\)\([0-9]\{2\}\)\([0-9]\{4\}\)'
   b=' \([0-9]\{2\}\):\([0-9]\{2\}\),/,\3\2\1\4\5,/g' 
   pattern="${a}${b}"
   sed -i "$pattern" $1
}   

function isolateData(){
   if [[ "$silent" == "false" ]] ; then
      echo "isolating required data..."
   fi
   # eliminates the lines we don't need
   sed '/branded_food/!d' $1 > $2
   # eliminates the first unneeded parts of the lines left 
   sed -i 's/^.*\">//g' $2 
   # puts a , separator between the filename and date
   sed -i 's/<\/a>\s\+/,/g' $2 
   # concatenates the last two columns to ...:seconds, datasize
   sed -i 's/:\([0-9][0-9]\) \+\([0-9]\+\)/:\1,\2\n/g' $2 

   if [ "$debug" == "false" ] ; then
      rm $1
   fi
}

function getDirectoryFromWeb(){
   if [[ "$silent" == "false" ]] ; then
      echo "getting available file list from web..."
   fi
   > $1
   #gets the html file showing the directory structure
   wget -O $1 -np $2
}

function storeNewestEntry(){
   if [[ "$silent" == "false" ]] ; then
      echo "storing newest file name and date..."
   fi
   # https://unix.stackexchange.com/questions/170204/find-the-max-value-of-column-1-and-print-respective-record-from-column-2-from-fill
   newest=$(sort -t ',' -nrk2,2 $1 | head -1)
   sed -i "s/^CURRENTLATEST:.*/CURRENTLATEST:$newest/g" "$SETTINGS"
   CURRENTLATEST=$newest

   if [ "$debug" == "false" ] ; then
      rm $1
   fi
}

function getFDAData(){
   if [[ "$silent" == "false" ]] ; then
      echo "downloading data..."
   fi
   ./downloadData.sh $1 $2 -b
}

function getUpdateIfNew(){
   if [[ "$silent" == "false" ]] ; then
      echo "checking if new data is available..."
   fi
   datediff=$(($3 - $4))

   if [[ $datediff -gt 0 ]] ; then
      if [ "$1" == "-b" ] || [ "$silent" == "true" ] ; then
         getFDAData $1 $2 
      else
         printf "\tAn update is available:\n"
         datasize=$5
         let dataMB=$((datasize/1048576))
         dataMB=$((dataMB + 1))
         printf "\tNew file is about %d MB.\n" $dataMB
         printf "\tdownload new file? (Y/n): "
         read reply
         if [ "$reply" == "y" ] || [ "$reply" == "Y" ] ; then
            getFDAData "$1" "$2" 
         else
            printf "reply was %s, goodbye!\n" $reply
         fi
      fi
   else
      printf "...no updates available.\n"
   fi
}


if [[ -n $1 ]] ; then
   if [ "$1" == "-h" ] ; then
      printf "$HELP"
      exit 0
   elif [ "$1" == "-b" ] || [ "$1" == "-f" ] ; then
      debug=false
      if [[ "$silent" == "false" ]] ; then
         echo "debugging off"
      fi
   else
      printf "$USAGE"
      exit 1
   fi
fi


if [ "$silent" == "false" ] ; then
   checkSettings
fi

getDirectoryFromWeb $TMPLINKSFILE $FDC_DIR_ADDRESS
isolateData $TMPLINKSFILE $TMPFILELIST
convertMonthToDigits $TMPFILELIST
rearrangeDate $TMPFILELIST
storeNewestEntry $TMPFILELIST

IFS=',' read -r -a currarray <<< "$CURRENTLATEST"
IFS=',' read -r -a lastarray <<< "$LASTLATEST"

currfile=${currarray[0]}
currdate=${currarray[1]}
currsize=${currarray[2]}
lastdate=${lastarray[1]}

if [ "$1" == '-f' ] ; then
   getFDAData $currfile $FDC_DIR_ADDRESS 
elif [ "$SERVER_POPULATED" == "false" ] ; then
   getUpdateIfNew $currfile $FDC_DIR_ADDRESS $currdate $lastdate $currsize
fi

