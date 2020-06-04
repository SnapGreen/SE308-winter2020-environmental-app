#!/bin/bash

HELP="\tA script to check script variables against known good values\n"
USAGE="\tUsage: ./test_settings [-ns|-s|-h]\n"
HELP="${USAGE}\t\t-s: checks full path settings\n"
HELP="${HELP}\t\t-ns: checks settings relative to repo root\n"
HELP="${HELP}\t\t-h: print help\n"
TESTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd)"
SCRIPTDIR="${TESTDIR}/../scripts"
TESTDIR="${TESTDIR}/settings"
SUFFIX_OUT="_settings.out"
SUFFIX_EXP="_settings.txt"

if (( $# != 1 )); then
   printf "$USAGE"
   exit 1
elif [[ "$1" == "-h" ]] ; then
   printf "$HELP"
   exit 0
elif [[ "$1" == "-ns" ]] ; then
   TESTDIR="Backend/tests/settings"
   SCRIPTDIR="Backend/scripts"
   SUFFIX_OUT="_npm${SUFFIX_OUT}"
   SUFFIX_EXP="_npm${SUFFIX_EXP}"
elif [[ "$1" != "-s" ]] ; then
   printf "$USAGE"
   exit 1
fi

${SCRIPTDIR}/convertToJson.sh "$1" > "${TESTDIR}/convertToJson${SUFFIX_OUT}"
${SCRIPTDIR}/getFDAUpdate.sh "$1" > "${TESTDIR}/getFDAUpdate${SUFFIX_OUT}"
${SCRIPTDIR}/downloadData.sh fakefile fakeurl "$1" > "${TESTDIR}/downloadData${SUFFIX_OUT}"
${SCRIPTDIR}/distributeFiles.sh fakefile.zip "$1" > "${TESTDIR}/distributeFiles${SUFFIX_OUT}"
${SCRIPTDIR}/uploadToDB.sh "$1" > "${TESTDIR}/uploadToDB${SUFFIX_OUT}"
${SCRIPTDIR}/populateDB.sh "$1" > "${TESTDIR}/populateDB${SUFFIX_OUT}"
${SCRIPTDIR}/getEPAUpdate.sh "$1" > "${TESTDIR}/getEPAUpdate${SUFFIX_OUT}"
${SCRIPTDIR}/convertEPAData.sh fakedir/ fakefile.xls "$1" > "${TESTDIR}/convertEPAData${SUFFIX_OUT}"

diff "${TESTDIR}/convertToJson${SUFFIX_OUT}" "${TESTDIR}/convertToJson${SUFFIX_EXP}" 
res=$?
if [[ $res -ne 0 ]]; then
   exit 1
fi

diff "${TESTDIR}/getFDAUpdate${SUFFIX_OUT}" "${TESTDIR}/getFDAUpdate${SUFFIX_EXP}"
res=$?
if [[ $res -ne 0 ]]; then
   exit 2
fi

diff "${TESTDIR}/downloadData${SUFFIX_OUT}" "${TESTDIR}/downloadData${SUFFIX_EXP}"
res=$?
if [[ $res -ne 0 ]]; then
   exit 3
fi

diff "${TESTDIR}/distributeFiles${SUFFIX_OUT}" "${TESTDIR}/distributeFiles${SUFFIX_EXP}"
res=$?
if [[ $res -ne 0 ]]; then
   exit 4
fi

diff "${TESTDIR}/uploadToDB${SUFFIX_OUT}" "${TESTDIR}/uploadToDB${SUFFIX_EXP}"
res=$?
if [[ $res -ne 0 ]]; then
   exit 5
fi

diff "${TESTDIR}/populateDB${SUFFIX_OUT}" "${TESTDIR}/populateDB${SUFFIX_EXP}"
res=$?
if [[ $res -ne 0 ]]; then
   exit 6
fi

diff "${TESTDIR}/getEPAUpdate${SUFFIX_OUT}" "${TESTDIR}/getEPAUpdate${SUFFIX_EXP}"
res=$?
if [[ $res -ne 0 ]]; then
   exit 7
fi

diff "${TESTDIR}/convertEPAData${SUFFIX_OUT}" "${TESTDIR}/convertEPAData${SUFFIX_EXP}"
res=$?
if [[ $res -ne 0 ]]; then
   exit 8
fi
