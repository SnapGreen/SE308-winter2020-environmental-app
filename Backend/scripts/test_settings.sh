#!/bin/bash
SETTINGS="/home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/settings.txt"
TESTDIR=$(grep -oP '(?<=^TESTDIR:).*' $SETTINGS)
TESTDIR="${TESTDIR}settings/"

./convertToJson.sh -s > ${TESTDIR}convertToJson_settings.out
./getFDAUpdate.sh -s > ${TESTDIR}getFDAUpdate_settings.out
./downloadData.sh fakefile fakeurl -s > ${TESTDIR}downloadData_settings.out
./distributeFiles.sh fakefile.zip -s > ${TESTDIR}distributeFiles_settings.out
./uploadToDB.sh -s > ${TESTDIR}uploadToDB_settings.out
./populateDB.sh -s > ${TESTDIR}populateDB_settings.out

echo "checking convertToJson.sh settings:"
diff ${TESTDIR}convertToJson_settings.out ${TESTDIR}convertToJson_settings.txt 

echo "checking getFDAUpdate.sh settings:"
diff ${TESTDIR}getFDAUpdate_settings.out ${TESTDIR}getFDAUpdate_settings.txt

echo "checking downloadData.sh settings:"
diff ${TESTDIR}downloadData_settings.out ${TESTDIR}downloadData_settings.txt

echo "checking distributeFiles.sh settings:"
diff ${TESTDIR}distributeFiles_settings.out ${TESTDIR}distributeFiles_settings.txt

echo "checking uploadToDB.sh settings:"
diff ${TESTDIR}uploadToDB_settings.out ${TESTDIR}uploadToDB_settings.txt

echo "checking populateDB.sh settings:"
diff ${TESTDIR}populateDB_settings.out ${TESTDIR}populateDB_settings.txt

echo "If there is no output after each of the checks above, settings check passed"
