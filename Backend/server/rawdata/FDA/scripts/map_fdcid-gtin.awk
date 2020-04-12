BEGIN{ FS="|" }

{ printf("%06d|%014d\n",substr($1,2),$2) }

