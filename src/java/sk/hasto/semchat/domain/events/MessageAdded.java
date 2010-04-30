package sk.hasto.semchat.domain.events;

import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Message;

/**
 * Sprava bola pridana do segmentu.
 * @author Branislav Hasto
 */
public abstract class MessageAdded extends MessageProcessed
{

	public MessageAdded(ChatSegment segment, Message message)
	{
		super(segment, message);
	}

}
