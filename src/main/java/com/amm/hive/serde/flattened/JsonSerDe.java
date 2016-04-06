package com.amm.hive.serde.flattened;

import java.util.*;
import java.io.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.SerDe;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.SerDeStats;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

/**
 * Ye old SerDe.
 */
public class JsonSerDe implements SerDe {
  private StructTypeInfo rowTypeInfo;
  private ObjectInspector objectInspector;
  private List<Object> row = new ArrayList<>();
  private JsonProcessor jsonProcessor = new JsonProcessor();
  
  @Override
  public void initialize(Configuration conf, Properties props) throws SerDeException {
    String pvalue = props.getProperty(serdeConstants.LIST_COLUMNS);
    List<String> columnNames = Arrays.asList(pvalue.split(","));
    
    pvalue = props.getProperty(serdeConstants.LIST_COLUMN_TYPES);
    List<TypeInfo> columnTypes = TypeInfoUtils.getTypeInfosFromTypeString(pvalue);
    
    rowTypeInfo = (StructTypeInfo) TypeInfoFactory.getStructTypeInfo(columnNames, columnTypes);
    objectInspector = TypeInfoUtils.getStandardJavaObjectInspectorFromTypeInfo(rowTypeInfo);
  }

  @Override
  public Object deserialize(Writable writable) throws SerDeException {
    row.clear();
    try {
      Map<String, Object> map = jsonProcessor.process(writable.toString());
      for (String fieldName : rowTypeInfo.getAllStructFieldNames()) {
        Object value= map.get(fieldName);
        row.add(value);
      }
    } catch (IOException e) {
      throw new SerDeException(e);
    }
    return row;
  }

  @Override
  public Writable serialize(Object obj, ObjectInspector objectInspector) throws SerDeException {
    try {
      return new Text(jsonProcessor.writeValue(obj));
    } catch (Exception e) {
      throw new SerDeException(e);
    }
  }

  @Override
  public Class<? extends Writable> getSerializedClass() {
    return Text.class;
  }

  @Override
  public SerDeStats getSerDeStats() {
    return null;
  }

  @Override
  public ObjectInspector getObjectInspector() throws SerDeException {
    return objectInspector;
  }
}
