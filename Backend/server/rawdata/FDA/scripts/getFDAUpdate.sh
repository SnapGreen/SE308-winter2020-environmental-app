#!/bin/bash
CURRENTFILE=$(grep -oP '(?<=CURRENTFILE:).*' settings.txt)
LASTFILE=$(grep -oP '(?<=LASTFILE:).*' settings.txt)
FDC_DIR_ADDRESS=$(grep -oP '(?<=FDC_DIR_ADDRESS:).*' settings.txt)
TMPFILE="links.tmp"
FILELIST="available_data.csv"
USAGE="./getFDAUpdate.sh [-flag]"
HELP="${USAGE}\n\t-f: force download\n\t-d: download if update available, no prompt\n"

function convertMonthToDigits(){
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
   a='s/,\([0-9]\{2\}\)\([0-9]\{2\}\)\([0-9]\{4\}\)'
   b=' \([0-9]\{2\}\):\([0-9]\{2\}\),/,\3\2\1\4\5,/g' 
   pattern="${a}${b}"
   sed -i "$pattern" $1
}   


if [[ "$1" == "-h" ]] ; then
   printf "$HELP"
elif [[ -z $1 ]] || [[ "$1" == "-f" ]] || [[ "$1" == "-d" ]] ; then
   #gets the html file showing the directory structure
   wget -O $TMPFILE -np $FDC_DIR_ADDRESS

   # eliminates the lines we don't need
   sed '/branded_food/!d' $TMPFILE > $FILELIST
   rm $TMPFILE

   # eliminates the first unneeded parts of the lines left 
   sed -i 's/^.*\">//g' $FILELIST 
   # puts a , separator between the filename and date
   sed -i 's/<\/a>\s\+/,/g' $FILELIST 
   # concatenates the last two columns to ...:seconds, datasize
   sed -i 's/:\([0-9][0-9]\) \+\([0-9]\+\)/:\1,\2\n/g' $FILELIST 

   convertMonthToDigits $FILELIST
   rearrangeDate $FILELIST

   # https://unix.stackexchange.com/questions/170204/find-the-max-value-of-column-1-and-print-respective-record-from-column-2-from-fill
   sort -t ',' -nrk2,2 $FILELIST | head -1 > $CURRENTFILE
   rm $FILELIST

   filename=$(cut -d , -f 1 $CURRENTFILE)

   if [[ "$1" == '-f' ]] ; then
      ./downloadData.sh $filename $FDC_DIR_ADDRESS
      cat $CURRENTFILE >> $LASTFILE
   else
      currentdate=$(cut -d , -f 2 $CURRENTFILE)
      lastdate=$(cut -d , -f 2 $LASTFILE)
      datediff=$((currentdate - lastdate))

      if [[ "$1" == "-d" ]] ; then
         ./downloadData.sh $filename $FDC_DIR_ADDRESS
         cat $CURRENTFILE >> $LASTFILE
      elif [[ $datediff -gt 0 ]] ; then
         printf "\tAn update is available:\n"
         datasize=$(cut -d , -f 3 $CURRENTFILE | tr -d '\n')
         let dataMB=datasize/1048576
         printf "\tdownloading will overwrite last file, if present.\n"
         printf "\tNew file is about %d MB.\n" $dataMB
         printf "\tdownload new file? (Y/n): "
         read reply
         if [ $reply == "y" ] || [ $reply == "Y" ] ; then
            ./downloadData.sh $filename $FDC_DIR_ADDRESS
            cat $CURRENTFILE > $LASTFILE
         else
            printf "reply was %s, goodbye!\n" $reply
         fi
      else
         printf "...no updates available.\n"
      fi
   fi
else
   print $USAGE
fi

