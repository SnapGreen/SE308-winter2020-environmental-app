#!/bin/bash
THISPATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd)"
TESTDIR="${THISPATH}/../tests/settings"
${THISPATH}/convertEPAData.sh fakedir/ fakefile.xls -s > ${TESTDIR}/convertEPAData_settings.txt
${THISPATH}/convertToJson.sh -s > ${TESTDIR}/convertToJson_settings.txt
${THISPATH}/distributeFiles.sh fakefile.zip -s > ${TESTDIR}/distributeFiles_settings.txt
${THISPATH}/downloadData.sh fakefile fakeurl -s > ${TESTDIR}/downloadData_settings.txt
${THISPATH}/getFDAUpdate.sh -s > ${TESTDIR}/getFDAUpdate_settings.txt
${THISPATH}/getEPAUpdate.sh -s > ${TESTDIR}/getEPAUpdate_settings.txt
${THISPATH}/populateDB.sh -s > ${TESTDIR}/populateDB_settings.txt
${THISPATH}/uploadToDB.sh -s > ${TESTDIR}/uploadToDB_settings.txt
