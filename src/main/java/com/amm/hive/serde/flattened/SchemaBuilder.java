package com.amm.hive.serde.flattened;

import java.util.*;
import java.io.*;

/**
 * Standalone main program that generates Hive schema for JSON files.
 */
public class SchemaBuilder {
  private JsonProcessor jsonProcessor = new JsonProcessor();
  private Map<Class,String> typeMap = new HashMap<>();
  private String serdeClassName = "com.amm.hive.serde.flattened.JsonSerDe" ;
  private String tableName ;
  private String location ;

  public static void main(String[] args) throws Exception {
    new SchemaBuilder().process(args);
  }

  private void process(String [] args) throws Exception {
    init();
    if (args.length < 3) {
      error("Usage: SchemaBuilder TABLE_NAME LOCATION FILES");
      return ;
    }
    tableName = args[0];
    location = args[1];
    for (int j=2 ; j < args.length ; j++) {
      process(new File(args[j]));
    }
  }

  private void process(File file) throws Exception {
    Map<String, Class> columns = new LinkedHashMap<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String record ;
      while ((record = reader.readLine()) != null ) {
        process(record,columns);
      }
    }
    //dump(columns);
    createDdl(columns);
  }

  private void process(String record, Map<String, Class> allColumns) throws IOException {
    Map<String, Object> recordColumns = jsonProcessor.process(record);
    for (Map.Entry<String,Object> entry : recordColumns.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (value == null) {
        //warn("Null value for "+key);
      } else { 
        Class type = allColumns.get(key);
        if (type == null) {
          allColumns.put(key,value.getClass());
        } else if (type != value.getClass()) {
          warn("Column "+key+" has clashing types: "+type.getName()+" "+value.getClass().getName());
        }
      }
    }
  }

  private void createDdl(Map<String,Class> map) {
    int j=0;
    info("CREATE EXTERNAL TABLE "+tableName+" (");
    for (Map.Entry<String,Class> entry : map.entrySet()) {
      String sep = j++ < map.size()-1 ? "," : "" ;
      String key = entry.getKey();
      Class typeJava = entry.getValue();
      String typeHive = typeMap.get(typeJava);
      if (typeHive == null) {
        warn("  // No Hive type for Java type "+typeJava);
      } else {
        info("  "+key+" "+typeHive+sep);
      }
    }
    info(")");
    info("ROW FORMAT SERDE '"+ serdeClassName + "'");
    info("LOCATION '"+location +"';" );
  }

  private void dump(Map<String,Class> map) {
    info("Columns - #="+map.size());
    for (Map.Entry<String,Class> entry : map.entrySet()) {
      info("  "+entry.getKey()+"="+entry.getValue().getName());
    }
  }

  private void init() {
    typeMap.put(Integer.class,"int");
    typeMap.put(Long.class,"bigint");
    typeMap.put(String.class,"string");
    typeMap.put(Boolean.class,"boolean");
    typeMap.put(Double.class,"double");
  }

  private static void info(Object o) { System.out.println(o);}
  private static void warn(Object o) { System.out.println("WARNING: "+o);}
  private static void error(Object o) { System.out.println("ERROR: "+o);}
}
