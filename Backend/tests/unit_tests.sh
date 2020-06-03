#!/bin/bash

testEquality(){
   assertEquals 1 1
}

testSettings(){
   ./Backend/tests/test_settings.sh
   ./Backend/tests/test_epa_convert.sh
}

. shunit2
