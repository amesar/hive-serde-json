package com.amm.hive.serde.flattened;

import java.util.*;
import org.apache.log4j.Logger;

/**
 * Flattens a nested map of objects to one level.
 */
public class Flattener {
	private static final Logger logger = Logger.getLogger(Flattener.class);
	private String separator = "_" ;

	public Flattener() {
	}

	public Flattener(String separator) {
		this.separator = separator ;
	}

	public Map<String, Object> flatten(Map<String, Object> map) {
		Map<String,Object> fmap = new LinkedHashMap<>();
		flatten(map,fmap,null);
		return fmap;
	}

	public void flatten(Map<String, Object> map, Map<String, Object> fmap, String parent) {
		for (Map.Entry<String,Object> entry : map.entrySet() ) {
			flatten(entry.getKey(), entry.getValue(), fmap, parent);
		}
	}

	@SuppressWarnings("unchecked")
	private void flatten(String key, Object value, Map<String, Object> fmap, String parent) {
		String ckey = makeCompositeName(parent,key);
		ckey = normalizeName(ckey);
		if (value instanceof Map) {
			flatten((Map<String, Object>)value, fmap, ckey);
		} else if (value instanceof List) {
			List<Object> list = (List<Object>)value;
			int j=0;
			for (Object elt : list) {
				String ekey = makeCompositeName(ckey,""+j);
				if (elt instanceof Map) {
					flatten((Map<String, Object>)elt, fmap, ekey);
				} else if (elt instanceof List) {
					flatten(key, elt, fmap, parent);
				} else {
					fmap.put(ekey,elt);
				}
				j++;
			}
		} else {
			fmap.put(ckey,value);
		}
	}

	private static final char BACKTICK = '`' ;

	// Hive doesn't like underscores at beginning of column names

	private String normalizeName(String name) { 
		name = name.toLowerCase();
		name = name.replaceAll("[\\W]|_", separator).replaceAll("^"+separator,"");
		if (HiveReservedKeywords.WORDS.contains(name)) {
			return name + separator ; 
			//return BACKTICK + name + BACKTICK ;
		}
		return name;
	}

	private String makeCompositeName(String base, String leaf) {
		return base==null ? leaf : base + separator + leaf;
	}
}
