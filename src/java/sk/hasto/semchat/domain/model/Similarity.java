package sk.hasto.semchat.domain.model;

import gate.creole.ontology.OURI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.Validate;

/**
 * Podobnost dvoch segmentov.
 * Podobnost je v intervale <0; 1>
 * @author Branislav Hasto
 */
public final class Similarity implements Comparable<Similarity>
{
	/** hodnota podobnosti */
	private final float value;

	/** prienik ontologickych tried v porovnavanych segmentoch */
	private final Set<OURI> classesJoin;

	/** zdrojovy segment */
	private final ChatSegment source;

	/** cielovy segment */
	private final ChatSegment target;


	/**
	 * Vyjadruje hodnotu podobnosti.
	 * Presne: vyjadruje mieru, do akej je cielovy segment podobny zdrojovemu.
	 * Platne hodnoty podobnosti su od 0 do 1.
	 * @param source zdrojovy segment
	 * @param target cielovy segment
	 * @param value
	 * @param classesJoin
	 */
	public Similarity(ChatSegment source, ChatSegment target,
					  float value, Set<OURI> classesJoin)
	{
		Validate.isTrue(value >= 0 && value <= 1, "Value must be between 0 and 1.");
		Validate.notNull(classesJoin, "Classes join must not be null.");
		this.source = source;
		this.target = target;
		this.value = value;
		this.classesJoin = new HashSet<OURI>(classesJoin);
	}


	/**
	 * @return hodnota podobnosti
	 */
	public float getValue()
	{
		return value;
	}


	public int compareTo(Similarity other)
	{
		Validate.notNull(other, "Cannot compare to null similarity object.");
		return Float.compare(value, other.value);
	}


	/**
	 * @return prienik ontologickych tried v porovnavanych segmentoch
	 */
	public Set<OURI> getClassesJoin()
	{
		return Collections.unmodifiableSet(classesJoin);
	}


	/**
	 * @return zdrojovy segment
	 */
	public ChatSegment getSource()
	{
		return source;
	}


	/**
	 * @return cielovy segment
	 */
	public ChatSegment getTarget()
	{
		return target;
	}

	
	@Override
	public String toString()
	{
		return String.valueOf(value * 100) + "%";
	}

}
