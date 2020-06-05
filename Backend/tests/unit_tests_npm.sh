#!/bin/bash

THISDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd)"

testEquality(){
   assertEquals 1 1
}

testSettings(){
   ${THISDIR}/test_settings.sh -ns
}

testEPAConvert(){
   ${THISDIR}/test_epa_convert.sh -n
}

. shunit2-2.1.6/src/shunit2
