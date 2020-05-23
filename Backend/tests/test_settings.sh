#!/bin/bash
SETTINGS="/home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/settings.txt"
TESTDIR=$(grep -oP '(?<=^TESTDIR:).*' $SETTINGS)
SETTTINGSTESTDIR="${TESTDIR}settings/"

./../scripts/convertToJson.sh -s > ${SETTTINGSTESTDIR}convertToJson_settings.out
./../scripts/getFDAUpdate.sh -s > ${SETTTINGSTESTDIR}getFDAUpdate_settings.out
./../scripts/downloadData.sh fakefile fakeurl -s > ${SETTTINGSTESTDIR}downloadData_settings.out
./../scripts/distributeFiles.sh fakefile.zip -s > ${SETTTINGSTESTDIR}distributeFiles_settings.out
./../scripts/uploadToDB.sh -s > ${SETTTINGSTESTDIR}uploadToDB_settings.out
./../scripts/populateDB.sh -s > ${SETTTINGSTESTDIR}populateDB_settings.out

echo "checking convertToJson.sh settings:"
diff ${SETTTINGSTESTDIR}convertToJson_settings.out ${SETTTINGSTESTDIR}convertToJson_settings.txt 

echo "checking getFDAUpdate.sh settings:"
diff ${SETTTINGSTESTDIR}getFDAUpdate_settings.out ${SETTTINGSTESTDIR}getFDAUpdate_settings.txt

echo "checking downloadData.sh settings:"
diff ${SETTTINGSTESTDIR}downloadData_settings.out ${SETTTINGSTESTDIR}downloadData_settings.txt

echo "checking distributeFiles.sh settings:"
diff ${SETTTINGSTESTDIR}distributeFiles_settings.out ${SETTTINGSTESTDIR}distributeFiles_settings.txt

echo "checking uploadToDB.sh settings:"
diff ${SETTTINGSTESTDIR}uploadToDB_settings.out ${SETTTINGSTESTDIR}uploadToDB_settings.txt

echo "checking populateDB.sh settings:"
diff ${SETTTINGSTESTDIR}populateDB_settings.out ${SETTTINGSTESTDIR}populateDB_settings.txt

echo "If there is no output after each of the checks above, settings check passed"
