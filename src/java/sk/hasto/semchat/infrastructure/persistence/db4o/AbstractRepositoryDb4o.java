package sk.hasto.semchat.infrastructure.persistence.db4o;

import com.db4o.ObjectContainer;
import org.apache.commons.lang.Validate;
import sk.hasto.semchat.domain.Repository;

/**
 * @author Branislav Hasto
 */
public abstract class AbstractRepositoryDb4o<T> implements Repository<T>
{
	/** db4o connection */
	protected final ObjectContainer db;


	public AbstractRepositoryDb4o(ObjectContainer db)
	{
		Validate.notNull(db, "Database connection is null.");
		this.db = db;
		this.db.ext().configure().callbacks(false);
	}


	public final void store(T entity)
	{
		Validate.notNull(db, "Entity to store is null.");
		db.store(entity);
	}

}
