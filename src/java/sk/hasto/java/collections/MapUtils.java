package sk.hasto.java.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang.Validate;

/**
 * Utility functions for working with Maps.
 * @author Branislav Hasto
 */
public final class MapUtils
{

	/**
	 * Sorts map by values in ascending order.
	 * @param <K> map keys type
	 * @param <V> map values type
	 * @param map
	 * @return
	 */
	public static <K, V extends Comparable<V>> LinkedHashMap<K, V>
			sortMapByValue(Map<K, V> map)
	{
		Validate.notNull(map, "Map must not be null.");

		List<Entry<K, V>> sortedEntries = sortEntriesByValue(map.entrySet());
		LinkedHashMap<K, V> sortedMap = new LinkedHashMap<K, V>(map.size());
		for (Entry<K, V> entry : sortedEntries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}


	/**
	 * Sorts map entries by value in ascending order.
	 * @param <K> map keys type
	 * @param <V> map values type
	 * @param entries
	 * @return
	 */
	private static <K, V extends Comparable<V>> List<Entry<K, V>>
			sortEntriesByValue(Set<Entry<K, V>> entries)
	{
		List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(entries);
		Collections.sort(sortedEntries, new ValueComparator<V>());
		return sortedEntries;
	}


	/**
	 * Komparator podla hodnot v polozkach mapy.
	 * @param <V> typ hodnot
	 */
	private static class ValueComparator<V extends Comparable<V>>
			implements Comparator<Entry<?, V>>
	{
		public int compare(Entry<?, V> entry1, Entry<?, V> entry2)
		{
			return entry1.getValue().compareTo(entry2.getValue());
		}
	}

}
