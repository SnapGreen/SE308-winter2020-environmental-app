#Script usage:

1. copy the "branded_food.csv" file to this directory
2. in a linux shell, enter:
   ./convertBranded_FoodsToJson.sh

This will output a new file, "branded_food.json", which consists of a json key "gtin14ToIngredients" paired to a list of key-value pairs.

The format for those pairs is:
 {"<14 digit gtin number>": "<list of ingredients>"}


