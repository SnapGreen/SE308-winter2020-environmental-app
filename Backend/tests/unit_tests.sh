#!/bin/bash

testEquality(){
   assertEquals 1 1
}

testSettings(){
   ./Backend/tests/test_settings.sh
}

. shunit2
