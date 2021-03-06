package com.amm.hive.serde.flattened;

import java.util.*;

/**
 * Hive reserved key words. From: https://github.com/apache/hive/blob/trunk/ql/src/java/org/apache/hadoop/hive/ql/parse/HiveParser.g
 */
public class HiveReservedKeywords {
  public final static Set<String> WORDS = new HashSet<String>() {{
    add("true");
    add("false");
    add("all");
    add("none");
    add("default");
    add("and");
    add("or");
    add("not");
    add("like");
    add("asc");
    add("desc");
    add("order");
    add("by");
    add("group");
    add("where");
    add("from");
    add("as");
    add("select");
    add("distinct");
    add("insert");
    add("overwrite");
    add("outer");
    add("join");
    add("left");
    add("right");
    add("full");
    add("on");
    add("partition");
    add("partitions");
    add("table");
    add("tables");
    add("tblproperties");
    add("show");
    add("msck");
    add("directory");
    add("local");
    add("transform");
    add("using");
    add("cluster");
    add("distribute");
    add("sort");
    add("union");
    add("load");
    add("data");
    add("inpath");
    add("is");
    add("null");
    add("create");
    add("external");
    add("alter");
    add("describe");
    add("drop");
    add("rename");
    add("to");
    add("comment");
    add("boolean");
    add("tinyint");
    add("smallint");
    add("int");
    add("bigint");
    add("float");
    add("double");
    add("date");
    add("datetime");
    add("timestamp");
    add("string");
    add("binary");
    add("array");
    add("map");
    add("reduce");
    add("partitioned");
    add("clustered");
    add("sorted");
    add("into");
    add("buckets");
    add("row");
    add("format");
    add("delimited");
    add("fields");
    add("terminated");
    add("collection");
    add("items");
    add("keys");
    add("key_type");
    add("lines");
    add("stored");
    add("sequencefile");
    add("textfile");
    add("inputformat");
    add("outputformat");
    add("location");
    add("tablesample");
    add("bucket");
    add("out");
    add("of");
    add("cast");
    add("add");
    add("replace");
    add("columns");
    add("rlike");
    add("regexp");
    add("temporary");
    add("function");
    add("explain");
    add("extended");
    add("serde");
    add("with");
    add("serdeproperties");
    add("limit");
    add("set");
    add("properties");
    add("value_type");
    add("elem_type");
    add("defined");
    add("subquery");
    add("rewrite");
    add("id");
  }};
}
