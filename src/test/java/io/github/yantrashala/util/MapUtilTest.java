package io.github.yantrashala.util;

import java.util.Iterator;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import io.github.yantrashala.sc.util.MapUtil;

public class MapUtilTest {

	@Before
	public void setUp() throws Exception {

	}

	private int count(Iterable<?> iterable) {
		int i = 0;
		final Iterator<?> iterator = iterable.iterator();
		for (; iterator.hasNext(); ++i)
			iterator.next();
		return i;
	}

	@Test
	public void test() {
		MultiValueMap<String, String> mVMap = new LinkedMultiValueMap<>();
		mVMap.add("key1", "value1");
		mVMap.add("key1", "value2");
		mVMap.add("key2", "value3");
		mVMap.add("key3", "value4");
		mVMap.add("key4", "value5");
		mVMap.add("key4", "value6");
		mVMap.add("key4", "value7");

		Map<String, Iterable<String>> converetdMap = MapUtil.mapFrom(mVMap);
		Assert.assertNotNull(converetdMap);
		Assert.assertEquals(4, converetdMap.size());
		Assert.assertNotNull(converetdMap.get("key1"));
		Assert.assertTrue(Iterable.class.isAssignableFrom(converetdMap.get("key1").getClass()));
		Assert.assertEquals(2, count(converetdMap.get("key1")));
		Assert.assertEquals(1, count(converetdMap.get("key2")));
		Assert.assertEquals("value3", converetdMap.get("key2").iterator().next());
		Assert.assertEquals("value4", converetdMap.get("key3").iterator().next());
		Assert.assertEquals(1, count(converetdMap.get("key3")));
		Assert.assertEquals(3, count(converetdMap.get("key4")));

	}

}
