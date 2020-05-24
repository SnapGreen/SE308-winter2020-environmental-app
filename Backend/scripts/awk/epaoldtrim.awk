# tells awk to use "," as a delimiter
BEGIN { FS = "\",\""} 
# gsub = "global substitution":
# these convert the epa color-shape categories with scores
{gsub(/half green \[circle\]/, 1, $1)}
{gsub(/green \[circle\]/, 2, $1)}
{gsub(/yellow \[triangle\]/, -1, $1)}

# trims to "chemical name"|score
# first ; in ingreds helps for parsing in next step
{ printf("%s|%s\n", $3, $1) }
