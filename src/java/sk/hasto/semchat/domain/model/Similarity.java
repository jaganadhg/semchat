package sk.hasto.semchat.domain.model;

import gate.creole.ontology.OURI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.Validate;

/**
 * Vyjadruje podobnost dvoch segmentov.
 * Presne: vyjadruje, nakolko sa cielovy segment priblizuje k zdrojovemu.
 * @author Branislav Hasto
 */
public final class Similarity implements Comparable<Similarity>
{
	/** zdrojovy segment */
	private final ChatSegment source;

	/** cielovy segment */
	private final ChatSegment target;

	/** hodnota podobnosti - v intervale <0;1> */
	private final float similarity;

	/** spolocne triedy segmentov */
	private final Set<OURI> commonClasses;

	/** spolocne instancie segmentov */
	private final Set<OURI> commonInstances;


	/**
	 * @param source zdrojovy segment (original)
	 * @param target cielovy segment (podobny originalu)
	 * @param similarity hodnota podobnosti v intervale <0;1>
	 * @param commonClasses spolocne triedy segmentov
	 * @param commonInstances spolocne instancie segmentov
	 */
	public Similarity(ChatSegment source, ChatSegment target, float similarity,
					  Set<OURI> commonClasses, Set<OURI> commonInstances)
	{
		Validate.notNull(source, "Source segment is null.");
		Validate.notNull(target, "Target segment is null.");
		Validate.notNull(commonClasses, "Common classes set is null.");
		Validate.notNull(commonInstances, "Common instances set is null.");
		Validate.isTrue(similarity >= 0 && similarity <= 1, "Similarity must be between 0 and 1.");

		this.source = source;
		this.target = target;
		this.similarity = similarity;
		this.commonClasses = new HashSet<OURI>(commonClasses);
		this.commonInstances = new HashSet<OURI>(commonInstances);
	}


	/**
	 * @return hodnota podobnosti
	 */
	public float getValue()
	{
		return similarity;
	}


	public int compareTo(Similarity other)
	{
		Validate.notNull(other, "Cannot compare similarity to null.");
		return Float.compare(similarity, other.similarity);
	}


	/**
	 * @return spolocne triedy segmentov
	 */
	public Set<OURI> getCommonClasses()
	{
		return Collections.unmodifiableSet(commonClasses);
	}


	/**
	 * @return spolocne instancie segmentov
	 */
	public Set<OURI> getCommonInstances()
	{
		return Collections.unmodifiableSet(commonInstances);
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
		StringBuilder stringBuilder = new StringBuilder(500);
		stringBuilder.append("Source ID: ");
		stringBuilder.append(source.getId());
		stringBuilder.append("\nTarget ID: ");
		stringBuilder.append(target.getId());
		stringBuilder.append("\nSimilarity: ");
		stringBuilder.append((similarity * 100) + "%");
		stringBuilder.append("\nCommon classes: ");
		stringBuilder.append(commonClasses);
		stringBuilder.append("\nCommon instances: ");
		stringBuilder.append(commonInstances);

		return stringBuilder.toString();
	}

}
