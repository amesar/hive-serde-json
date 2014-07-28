package com.amm.hive.serde.flattened;

import java.util.*;
import org.apache.log4j.Logger;

/**
 * Flattens a nested map of objects to one level.
 */
public class Flattener {
	private static final Logger logger = Logger.getLogger(Flattener.class);
	private String separator = "_" ;
	private static final char BACKTICK = '`' ;

	public Flattener() {
	}

	public Flattener(String separator) {
		this.separator = separator ;
	}

	public Map<String, Object> flatten(Map<String, Object> map) {
		Map<String,Object> flattenedMap = new LinkedHashMap<>();
		flatten(map,flattenedMap,null);
		return flattenedMap;
	}

	public void flatten(Map<String, Object> map, Map<String, Object> flattenedMap, String parentKey) {
		for (Map.Entry<String,Object> entry : map.entrySet() ) {
			flatten(entry.getKey(), entry.getValue(), flattenedMap, parentKey);
		}
	}

	@SuppressWarnings("unchecked")
	private void flatten(String key, Object value, Map<String, Object> flattenedMap, String parentKey) {
		String compoundKey = normalizeName(makeCompoundName(parentKey,key));
		if (value instanceof Map) {
			flatten((Map<String, Object>)value, flattenedMap, compoundKey);
		} else if (value instanceof List) {
			List<Object> list = (List<Object>)value;
			int j=0;
			for (Object element : list) {
				String elementKey = makeCompoundName(compoundKey,""+j);
				if (element instanceof Map) {
					flatten((Map<String, Object>)element, flattenedMap, elementKey);
				} else if (element instanceof List) {
					flatten(key, element, flattenedMap, parentKey);
				} else {
					flattenedMap.put(elementKey,element);
				}
				j++;
			}
		} else {
			flattenedMap.put(compoundKey,value);
		}
	}

	private String normalizeName(String name) { 
		name = name.toLowerCase();
		name = name.replaceAll("[\\W]|_", separator).replaceAll("^"+separator,""); // Hive doesn't like underscores at beginning of column names
		if (HiveReservedKeywords.WORDS.contains(name)) {
			return name + separator ; 
			//return BACKTICK + name + BACKTICK ;
		}
		return name;
	}

	private String makeCompoundName(String base, String leaf) {
		return base==null ? leaf : base + separator + leaf;
	}
}
