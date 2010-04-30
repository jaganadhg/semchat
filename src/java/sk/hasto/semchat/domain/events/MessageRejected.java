package sk.hasto.semchat.domain.events;

import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Message;

/**
 * Sprava bola zamietnuta segmentom.
 * @author Branislav Hasto
 */
public final class MessageRejected extends MessageProcessed
{

	public MessageRejected(ChatSegment segment, Message message)
	{
		super(segment, message);
	}

}
