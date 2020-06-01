#!/bin/bash
SETTINGS="./Backend/scripts/settings.txt"
#TESTDIR=$(grep -oP '(?<=^TESTDIR:).*' $SETTINGS)
#TESTDIR="${TESTDIR}settings/"
TESTDIR="./Backend/tests/settings/"

./Backend/scripts/convertToJson.sh -s > "${TESTDIR}convertToJson_settings.out"
./Backend/scripts/getFDAUpdate.sh -s > "${TESTDIR}getFDAUpdate_settings.out"
./Backend/scripts/downloadData.sh fakefile fakeurl -s > "${TESTDIR}downloadData_settings.out"
./Backend/scripts/distributeFiles.sh fakefile.zip -s > "${TESTDIR}distributeFiles_settings.out"
./Backend/scripts/uploadToDB.sh -s > "${TESTDIR}uploadToDB_settings.out"
./Backend/scripts/populateDB.sh -s > "${TESTDIR}populateDB_settings.out"
./Backend/scripts/getEPAUpdate.sh -s > "${TESTDIR}getEPAUpdate_settings.out"
./Backend/scripts/convertEPAData.sh fakedir/ fakefile -s > "${TESTDIR}convertEPAData_settings.out"

#echo "checking convertToJson.sh settings..."
diff "${TESTDIR}convertToJson_settings.out" "${TESTDIR}convertToJson_settings.txt" 
res=$?
if [[ $res -ne 0 ]]; then
   exit 1
fi

#echo "checking getFDAUpdate.sh settings..."
diff "${TESTDIR}getFDAUpdate_settings.out" "${TESTDIR}getFDAUpdate_settings.txt"
res=$?
if [[ $res -ne 0 ]]; then
   exit 2
fi

#echo "checking downloadData.sh settings..."
diff "${TESTDIR}downloadData_settings.out" "${TESTDIR}downloadData_settings.txt"
res=$?
if [[ $res -ne 0 ]]; then
   exit 3
fi

#echo "checking distributeFiles.sh settings..."
diff "${TESTDIR}distributeFiles_settings.out" "${TESTDIR}distributeFiles_settings.txt"
res=$?
if [[ $res -ne 0 ]]; then
   exit 4
fi

#echo "checking uploadToDB.sh settings..."
diff "${TESTDIR}uploadToDB_settings.out" "${TESTDIR}uploadToDB_settings.txt"
res=$?
if [[ $res -ne 0 ]]; then
   exit 5
fi

#echo "checking populateDB.sh settings..."
diff "${TESTDIR}populateDB_settings.out" "${TESTDIR}populateDB_settings.txt"
res=$?
if [[ $res -ne 0 ]]; then
   exit 6
fi

#echo "checking getEPAUpdate.sh settings..."
diff "${TESTDIR}getEPAUpdate_settings.out" "${TESTDIR}getEPAUpdate_settings.txt"
res=$?
if [[ $res -ne 0 ]]; then
   exit 7
fi

#echo "checking convertEPAData.sh settings..."
diff "${TESTDIR}convertEPAData_settings.out" "${TESTDIR}convertEPAData_settings.txt"
res=$?
if [[ $res -ne 0 ]]; then
   exit 8
fi

#echo "If there is no output after each of the checks above, settings check passed"
