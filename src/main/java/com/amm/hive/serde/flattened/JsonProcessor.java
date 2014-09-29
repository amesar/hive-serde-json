package com.amm.hive.serde.flattened;

import java.util.*;
import java.io.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Processes JSON document and returns a map of flattened objects.
 */
public class JsonProcessor {
	private final ObjectMapper mapper = new ObjectMapper();
	private final Flattener flattener = new Flattener();
	
	public Map<String, Object> process(String content) throws IOException {
		Map<String, Object> map = mapper.readValue(content, new TypeReference<Map<String, Object>>(){});
		return flattener.flatten(map);
	}

	public String writeValue(Object obj) throws IOException {
		return mapper.writeValueAsString(obj);
	}
}
