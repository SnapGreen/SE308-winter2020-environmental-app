#!/bin/bash
SOURCEFILE=$1

printf "unzipping %s\n" $SOURCEFILE
unzip $SOURCEFILE

rm food.csv
rm food_attribute.csv
rm food_nutrient.csv
rm all_downloaded_table_record_counts.csv
rm Download*.pdf
rm $SOURCEFILE

./convertBrandedFoodToJson.sh

