#!/bin/bash

if [[ "$1" == "-t" ]]; then
   TESTDIR="Backend/tests/settings/"
   ./Backend/scripts/convertToJson.sh -t > "${TESTDIR}convertToJson_npm_settings.out"
   ./Backend/scripts/getFDAUpdate.sh -t > "${TESTDIR}getFDAUpdate_npm_settings.out"
   ./Backend/scripts/downloadData.sh fakefile fakeurl -t > "${TESTDIR}downloadData_npm_settings.out"
   ./Backend/scripts/distributeFiles.sh fakefile.zip -t > "${TESTDIR}distributeFiles_npm_settings.out"
   ./Backend/scripts/uploadToDB.sh -t > "${TESTDIR}uploadToDB_npm_settings.out"
   ./Backend/scripts/populateDB.sh -t > "${TESTDIR}populateDB_npm_settings.out"
   ./Backend/scripts/getEPAUpdate.sh -t > "${TESTDIR}getEPAUpdate_npm_settings.out"
   ./Backend/scripts/convertEPAData.sh fakedir/ fakefile.xls -t > "${TESTDIR}convertEPAData_npm_settings.out"

   diff "${TESTDIR}convertToJson_npm_settings.out" "${TESTDIR}convertToJson_npm_settings.txt" 
   res=$?
   if [[ $res -ne 0 ]]; then
      exit 1
   fi

   diff "${TESTDIR}getFDAUpdate_npm_settings.out" "${TESTDIR}getFDAUpdate_npm_settings.txt"
   res=$?
   if [[ $res -ne 0 ]]; then
      exit 2
   fi

   diff "${TESTDIR}downloadData_npm_settings.out" "${TESTDIR}downloadData_npm_settings.txt"
   res=$?
   if [[ $res -ne 0 ]]; then
      exit 3
   fi

   diff "${TESTDIR}distributeFiles_npm_settings.out" "${TESTDIR}distributeFiles_npm_settings.txt"
   res=$?
   if [[ $res -ne 0 ]]; then
      exit 4
   fi

   diff "${TESTDIR}uploadToDB_npm_settings.out" "${TESTDIR}uploadToDB_npm_settings.txt"
   res=$?
   if [[ $res -ne 0 ]]; then
      exit 5
   fi

   diff "${TESTDIR}populateDB_npm_settings.out" "${TESTDIR}populateDB_npm_settings.txt"
   res=$?
   if [[ $res -ne 0 ]]; then
      exit 6
   fi

   diff "${TESTDIR}getEPAUpdate_npm_settings.out" "${TESTDIR}getEPAUpdate_npm_settings.txt"
   res=$?
   if [[ $res -ne 0 ]]; then
      exit 7
   fi

   diff "${TESTDIR}convertEPAData_npm_settings.out" "${TESTDIR}convertEPAData_npm_settings.txt"
   res=$?
   if [[ $res -ne 0 ]]; then
      exit 8
   fi
else
   SETTINGS="/home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/settings.txt"
   TESTDIR=$(grep -oP '(?<=^TESTDIR:).*' $SETTINGS)
   TESTDIR="${TESTDIR}settings/"
   /home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/convertToJson.sh -s > "${TESTDIR}convertToJson_settings.out"
   /home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/getFDAUpdate.sh -s > "${TESTDIR}getFDAUpdate_settings.out"
   /home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/downloadData.sh fakefile fakeurl -s > "${TESTDIR}downloadData_settings.out"
   /home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/distributeFiles.sh fakefile.zip -s > "${TESTDIR}distributeFiles_settings.out"
   /home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/uploadToDB.sh -s > "${TESTDIR}uploadToDB_settings.out"
   /home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/populateDB.sh -s > "${TESTDIR}populateDB_settings.out"
   /home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/getEPAUpdate.sh -s > "${TESTDIR}getEPAUpdate_settings.out"
   /home/jtwedt/projSE308/SE308-winter2020-environmental-app/Backend/scripts/convertEPAData.sh fakedir/ fakefile.xls -s > "${TESTDIR}convertEPAData_settings.out"

   diff "${TESTDIR}convertToJson_settings.out" "${TESTDIR}convertToJson_settings.txt" 
   res=$?
   if [[ $res -ne 0 ]]; then
      exit 1
   fi

   diff "${TESTDIR}getFDAUpdate_settings.out" "${TESTDIR}getFDAUpdate_settings.txt"
   res=$?
   if [[ $res -ne 0 ]]; then
      exit 2
   fi

   diff "${TESTDIR}downloadData_settings.out" "${TESTDIR}downloadData_settings.txt"
   res=$?
   if [[ $res -ne 0 ]]; then
      exit 3
   fi

   diff "${TESTDIR}distributeFiles_settings.out" "${TESTDIR}distributeFiles_settings.txt"
   res=$?
   if [[ $res -ne 0 ]]; then
      exit 4
   fi

   diff "${TESTDIR}uploadToDB_settings.out" "${TESTDIR}uploadToDB_settings.txt"
   res=$?
   if [[ $res -ne 0 ]]; then
      exit 5
   fi

   diff "${TESTDIR}populateDB_settings.out" "${TESTDIR}populateDB_settings.txt"
   res=$?
   if [[ $res -ne 0 ]]; then
      exit 6
   fi

   diff "${TESTDIR}getEPAUpdate_settings.out" "${TESTDIR}getEPAUpdate_settings.txt"
   res=$?
   if [[ $res -ne 0 ]]; then
      exit 7
   fi

   diff "${TESTDIR}convertEPAData_settings.out" "${TESTDIR}convertEPAData_settings.txt"
   res=$?
   if [[ $res -ne 0 ]]; then
      exit 8
   fi
fi

