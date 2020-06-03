#!/bin/bash

testEquality(){
   assertEquals 1 1
}

testSettings(){
   ./Backend/tests/test_settings.sh "$1"
}

testEPAConvert(){
   ./Backend/tests/test_epa_convert.sh "$1"
}

testEquality
testSettings "$1"
testEPAConvert "$1"

