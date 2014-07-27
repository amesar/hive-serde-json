
. `dirname $0`/common.env

PGM=com.amm.hive.serde.flattened.SchemaBuilder

java  -cp $CPATH $PGM $* | tee log.txt
