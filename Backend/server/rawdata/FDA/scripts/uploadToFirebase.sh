#!/bin/bash
PRODS_PER_JSON=500
SUFFIX_LEN=4
FB_WRITES_PER_DAY=$(grep -oP "(?<=FB_WRITES_PER_DAY:).*" settings.txt)
SPLIT_PREFIX=$(grep -oP "(?<=SPLIT_PREFIX:).*" settings.txt)
OUT_SUFFIX=$(grep -oP "(?<=OUT_SUFFIX:).*" settings.txt)


#max_file_uploads=$((FB_WRITES_PER_DAY / PRODS_PER_JSON))

#shopt -s nullglob

#alljsons=(../branded_food_*.json)

#uploadfilenums="${alljsons[@]:0:$max_file_uploads}"
#echo "${uploadfilenums[@]}"

file="../bf.json"
curl --header "Content-Type: application/json" --request POST --data  @$file http://localhost:8080/products
#for num in $filearr; 
#do
#   filename="${SPLIT_PREFIX}$num${OUT_SUFFIX}"
#   echo $filename
#done

#app.post("/products", async function (req, res) {
#  if (!req.body || !req.body.products) {
#    res.status(401).json({
#      message: "No req.body or req.body.products present",
#    });
#  }
#
#  try {
#    // Need a bigger limit since we are handling a batch write containing 500 products
#    app.use(
#      bodyParser.json({
#        limit: "1mb",
#      })
#    );
#
#    await FIREBASE.productBatchWrite(req.body.products);
#    res.json({
#      message: `Product batch write successful`,
#    });
#  } catch (err) {
#    res.status(401).json({
#      message: "Product Batch Write Error",
#    });
#    console.log(err);
#  }
#});


