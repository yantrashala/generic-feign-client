package io.github.yantrashala.sc.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.MultiValueMap;

public interface MapUtil {

	/**
	 * Map values from a MultiValueMap retaining all values into an Iterable.
	 * 
	 * @param multiMap
	 * @return
	 */
	static <K, V> Map<K, Iterable<V>> mapFrom(MultiValueMap<K, V> multiMap) {
		Map<K, Iterable<V>> map = new LinkedHashMap<>();
		if (multiMap != null) {
			multiMap.forEach(map::put);
		}
		return map;
	}
}
