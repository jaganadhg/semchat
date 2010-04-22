package sk.hasto.semchat.domain.model;

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


	public Similarity(float value)
	{
		Validate.isTrue(value >= 0 && value <= 1, "Value must be between 0 and 1.");
		this.value = value;
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


	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Similarity other = (Similarity) obj;
		return value == other.value;
	}


	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 79 * hash + Float.floatToIntBits(this.value);
		return hash;
	}


	@Override
	public String toString()
	{
		return String.valueOf(value * 100) + "%";
	}

}
