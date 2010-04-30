package sk.hasto.semchat.domain.events;

import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Message;

/**
 * Sprava bez semantickeho vyznamu pridana do segmentu.
 * @author Branislav Hasto
 */
public class NonSemanticMessageAdded extends MessageAdded
{

	public NonSemanticMessageAdded(ChatSegment segment, Message message)
	{
		super(segment, message);
	}

}
