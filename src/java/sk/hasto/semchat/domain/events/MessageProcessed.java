package sk.hasto.semchat.domain.events;

import org.apache.commons.lang.Validate;
import sk.hasto.semchat.domain.DomainEvent;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Message;

/**
 * Sprava bola spracovana segmentom.
 * @author Branislav Hasto
 */
public abstract class MessageProcessed implements DomainEvent
{
	private final ChatSegment segment;
	private final Message message;


	public MessageProcessed(ChatSegment segment, Message message)
	{
		Validate.notNull(segment, "Segment is null.");
		Validate.notNull(segment, "Message is null.");
		this.segment = segment;
		this.message = message;
	}


	public final Message getMessage()
	{
		return message;
	}


	public final ChatSegment getSegment()
	{
		return segment;
	}

}
