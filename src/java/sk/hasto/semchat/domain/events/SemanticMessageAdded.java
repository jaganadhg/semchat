package sk.hasto.semchat.domain.events;

import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Message;

/**
 * Sprava so semantickym vyznamom pridana do segmentu.
 * @author Branislav Hasto
 */
public final class SemanticMessageAdded extends MessageAdded
{

	public SemanticMessageAdded(ChatSegment segment, Message message)
	{
		super(segment, message);
	}

}
