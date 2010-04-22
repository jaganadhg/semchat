package sk.hasto.semchat.domain.model;

import org.apache.commons.lang.Validate;
import sk.hasto.semchat.domain.common.Event;

/**
 * Udalost: Pridanie spravy do chatu.
 * @author Branislav Hasto
 */
public class MessageAddedEvent implements Event
{
	/** pridana sprava */
	private final Message message;


	/**
	 * @param message pridana sprava
	 */
	public MessageAddedEvent(Message message)
	{
		Validate.notNull(message, "Message must not be null.");
		this.message = message;
	}


	/**
	 * @return pridana sprava
	 */
	public Message getMessage()
	{
		return message;
	}
}
