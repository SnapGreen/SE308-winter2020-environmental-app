#!/bin/bash
./convertToJson.sh -s > ../tests/settings/convertToJson_settings.txt
./convertEPAData.sh fakedir/ fakefile.xls -s > ../tests/settings/convertEPAData_settings.txt
./distributeFiles.sh fakefile.zip -s > ../tests/settings/distributeFiles_settings.txt
./downloadData.sh fakefile fakeurl -s > ../tests/settings/downloadData_settings.txt
./getFDAUpdate.sh -s > ../tests/settings/getFDAUpdate_settings.txt
./populateDB.sh -s > ../tests/settings/populateDB_settings.txt
./getEPAUpdate.sh -s > ../tests/settings/getEPAUpdate_settings.txt
./convertEPAData.sh fakedir/ fakefile.xls -s > ../tests/convertEPAData_settings.txt
