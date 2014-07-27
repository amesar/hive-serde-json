
PGM=com.amm.hive.serde.flattened.SchemaBuilder

java  -cp target/amm-serde-json-1.0-SNAPSHOT.jar $PGM $* | tee log.txt
