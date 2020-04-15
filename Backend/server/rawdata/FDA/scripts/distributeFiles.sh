#!/bin/bash
ZIPFILE=$1
DATASOURCE=$(grep -oP '(?<=DATASOURCE:).*' settings.txt)
UPDATESOURCE=$(grep -oP '(?<=UPDATESOURCE:).*' settings.txt)

printf "unzipping %s\n" $ZIPFILE
unzip $ZIPFILE

rm food.csv
rm food_attribute.csv
rm food_nutrient.csv
rm all_downloaded_table_record_counts.csv
rm Download*.pdf
rm $ZIPFILE

mv $DATASOURCE ..
mv $UPDATESOURCE ..

