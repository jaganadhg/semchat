package sk.hasto.java.collections;

import java.util.HashSet;
import java.util.Set;

/**
 * Pomocne funkcie na pracu s mnozinami.
 * @author Branislav Hasto
 */
public final class SetUtils
{

	/**
	 * Vytvori prienik mnozin.
	 * @param <E>
	 * @param set1
	 * @param set2
	 * @return
	 */
	public static <E> Set<E> join(Set<E> set1, Set<E> set2)
	{
		Set<E> join = new HashSet<E>(set1);
		join.retainAll(set2);
		return join;
	}


	/**
	 * Vytvori zjednotenie mnozin.
	 * @param <E>
	 * @param set1
	 * @param set2
	 * @return
	 */
	public static <E> Set<E> union(Set<E> set1, Set<E> set2)
	{
		Set<E> union = new HashSet<E>(set1);
		union.addAll(set2);
		return union;
	}
	
}
