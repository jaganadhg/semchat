package sk.hasto.semchat.domain.model;

import org.apache.commons.lang.Validate;

/**
 * Pouzivatel chatu.
 * @author Branislav Hasto
 */
public final class User
{
	/** meno pouzivatela */
	private final String name;


	/**
	 * @param name meno pouzivatela
	 * @throws IllegalArgumentException ak je name null alebo prazdne
	 */
	public User(String name)
	{
		Validate.notEmpty(name, "Name must not be null or empty.");
		this.name = name;
	}


	/**
	 * @return meno pouzivatela
	 */
	public String getName()
	{
		return name;
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
		final User other = (User) obj;
		return name.equals(other.name);
	}


	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 61 * hash + name.hashCode();
		return hash;
	}

}
