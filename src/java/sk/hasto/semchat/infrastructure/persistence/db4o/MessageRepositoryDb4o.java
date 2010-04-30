package sk.hasto.semchat.infrastructure.persistence.db4o;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
import java.util.Comparator;
import java.util.List;
import sk.hasto.semchat.domain.model.Message;
import sk.hasto.semchat.domain.services.repositories.MessageRepository;

/**
 * @author Branislav Hasto
 */
public final class MessageRepositoryDb4o extends AbstractRepositoryDb4o<Message>
										 implements MessageRepository
{

	/** comparator na zoradenie sprav zostupne podla datumu */
	private static final Comparator<Message> descendingMessageDateComparator;

	/** predikat, ktory vyberie vsetky spravy */
	private static final Predicate<Message> allMessagesPredicate;

	
	static {
		descendingMessageDateComparator = new Comparator<Message>() {
			public int compare(Message message1, Message message2) {
				return message2.getTime().compareTo(message1.getTime());
			}
		};

		allMessagesPredicate = new Predicate<Message>() {
			private static final long serialVersionUID = 5765212804928448418L;
			@Override
			public boolean match(Message candidate) {
				return true;
			}
		};
	}


	public MessageRepositoryDb4o(ObjectContainer db)
	{
		super(db);
	}


	public List<Message> getLast(int count)
	{
		List<Message> messages = db.query(allMessagesPredicate, descendingMessageDateComparator);
		int toIndex = Math.min(messages.size(), count);
		return messages.subList(0, toIndex);
	}

}
