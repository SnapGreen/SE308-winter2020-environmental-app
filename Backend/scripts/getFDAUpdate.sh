#!/bin/bash
debug=true
fin=false
silent=false

function checkSettingsNPM(){
   SETTINGS_NPM="./Backend/scripts/settings_npm.txt"
   CURRENTLATEST=$(grep -oP '(?<=^CURRENTLATEST:).*' $SETTINGS_NPM)
   LASTLATEST=$(grep -oP '(?<=^LASTLATEST:).*' $SETTINGS_NPM)
   SERVER_POPULATED=$(grep -oP '(?<=^SERVER_POPULATED:).*' $SETTINGS_NPM)
   FDC_DIR_ADDRESS=$(grep -oP '(?<=^FDC_DIR_ADDRESS:).*' $SETTINGS_NPM)
   TMPDIR=$(grep -oP '(?<=^TMPDIR:).*' $SETTINGS_NPM)
   TMPFILE_END=$(grep -oP '(?<=^TMPFILE_END:).*' $SETTINGS_NPM)
   TMPLINKSFILE="${TMPDIR}links${TMPFILE_END}"
   TMPFILELIST="${TMPDIR}available_data${TMPFILE_END}"
   USAGE="\t\tUsage: ./getFDAUpdate.sh [OPTION] (use option -h for help)\n"
   HELP="${USAGE}\t\t**If no OPTION supplied, debug mode on (temp files remain)\n"
   HELP="${HELP}\t\t\t-b: bypass debug mode, download only if new\n"
   HELP="${HELP}\t\t\t-f: force download\n"
   HELP="${HELP}\t\t\t-s: output settings only\n"
   HELP="${HELP}\t\t\t-h: print help\n"

   echo "settings check:"
   printf "\tSETTINGS: %s\n" $SETTINGS_NPM
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
   if [ "$1" == "-t" ] ; then
      checkSettingsNPM
      exit 0
   fi
fi

SETTINGS="/home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/settings.txt"
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
HELP="${HELP}\t\t\t-s: output settings only\n"
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

function convertMonthToDigits(){
   echo "converting months to digit format..."
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
   echo "rearranging date..."
   a='s/,\([0-9]\{2\}\)\([0-9]\{2\}\)\([0-9]\{4\}\)'
   b=' \([0-9]\{2\}\):\([0-9]\{2\}\),/,\3\2\1\4\5,/g' 
   pattern="${a}${b}"
   sed -i "$pattern" $1
}   

function isolateData(){
   echo "isolating required data..."
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
   echo "getting available file list from web..."
   > $1
   #gets the html file showing the directory structure
   wget -O $1 -np $2
}

function storeNewestEntry(){
   echo "storing newest file name and date..."
   # https://unix.stackexchange.com/questions/170204/find-the-max-value-of-column-1-and-print-respective-record-from-column-2-from-fill
   newest=$(sort -t ',' -nrk2,2 $1 | head -1)
   sed -i "s/^CURRENTLATEST:.*/CURRENTLATEST:$newest/g" "$SETTINGS"
   CURRENTLATEST=$newest

   if [ "$debug" == "false" ] ; then
      rm $1
   fi
}

function getFDAData(){
   echo "downloading data..."
   ./downloadData.sh $1 $2 -b
}

function getUpdateIfNew(){
   echo "checking if new data is available..."
   datediff=$(($3 - $4))

   if [[ $datediff -gt 0 ]] ; then
      if [ "$1" == "-b" ] ; then
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
            getFDAData $1 $2 
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
fi

