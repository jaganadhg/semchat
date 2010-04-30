package sk.hasto.semchat.domain;

/**
 * Generic repository.
 * @author Branislav Hasto
 */
public interface Repository<T>
{
	/**
	 * Stores entity.
	 * @param entity
	 */
	void store(T entity);
}
