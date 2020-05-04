#!/bin/bash
SETTINGS="settings.txt"
DATADIR=$(grep -oP "(?<=^DATADIR:).*" $SETTINGS)
FDADIR=$(grep -oP "(?<=^FDADIR:).*" $SETTINGS)
FDADATADIR="${DATADIR}${FDADIR}"
SUFFIX_LEN=$(grep -oP "(?<=^SUFFIX_LEN:).*" $SETTINGS)
SUFFIX_LEN=$(grep -oP "(?<=^SUFFIX_LEN:).*" $SETTINGS)
PRODS_PER_JSON=$(grep -oP "(?<=^PRODS_PER_JSON:).*" $SETTINGS)
FB_WRITES_PER_DAY=$(grep -oP "(?<=^FB_WRITES_PER_DAY:).*" $SETTINGS)
SPLIT_PREFIX=$(grep -oP "(?<=^SPLIT_PREFIX:).*" $SETTINGS)
OUTFILE_END=$(grep -oP "(?<=^OUTFILE_END:).*" $SETTINGS)
UPLOAD_SLEEP=$(grep -oP "(?<=^UPLOAD_SLEEP:).*" $SETTINGS)
LOGDIR=$(grep -oP "(?<=^LOGDIR:).*" $SETTINGS)
UPLOADLOGDIR="${LOGDIR}uploads/"
LASTUPLOAD=$(grep -oP "(?<=^LASTUPLOAD:).*" $SETTINGS)
SERVER_POPULATED=$(grep -oP "(?<=^SERVER_POPULATED:).*" $SETTINGS)
DONE_UPLOADING=$(grep -oP "(?<=^DONE_UPLOADING:).*" $SETTINGS)
USAGE="\t\tUsage: ./uploadToDB.sh [OPTIONS] (-h for help)\n"
HELP="${USAGE}\t\tOPTIONS:\n"
HELP="${HELP}\t\t\t-b: upload without prompt\n"
HELP="${HELP}\t\t\t-s: print settings only\n"
HELP="${HELP}\t\t\t-h: print help\n"

fin=false
prompt=true

function checkSettings(){
   echo "settings check:"
   printf "\tSETTINGS: %s\n" "$SETTINGS"
   printf "\tDATADIR: %s\n" "$DATADIR"
   printf "\tFDADIR: %s\n" "$FDADIR"
   printf "\tFDADATADIR: %s\n" "$FDADATADIR"
   printf "\tSUFFIX_LEN: %s\n" "$SUFFIX_LEN"
   printf "\tPRODS_PER_JSON: %s\n" "$PRODS_PER_JSON"
   printf "\tFB_WRITES_PER_DAY: %s\n" "$FB_WRITES_PER_DAY"
   printf "\tSPLIT_PREFIX: %s\n" "$SPLIT_PREFIX"
   printf "\tOUTFILE_END: %s\n" "$OUTFILE_END"
   printf "\tUPLOAD_SLEEP: %s\n" "$UPLOAD_SLEEP"
   printf "\tLOGDIR: %s\n" "$LOGDIR"
   printf "\tUPLOADLOGDIR: %s\n" "$UPLOADLOGDIR"
   printf "\tLASTUPLOAD: %s\n" "$LASTUPLOAD"
   printf "\tSERVER_POPULATED: %s\n" "$SERVER_POPULATED"
   printf "\tDONE_UPLOADING: %s\n" "$DONE_UPLOADING"
   printf "\tUSAGE:\n"
   printf "$USAGE"
   printf "\tHELP:\n"
   printf "$HELP"
}


readyNextUpdate

