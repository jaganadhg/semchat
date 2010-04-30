package sk.hasto.semchat.domain.events;

import org.apache.commons.lang.Validate;
import sk.hasto.semchat.domain.DomainEvent;
import sk.hasto.semchat.domain.model.ChatSegment;

/**
 * @author Branislav Hasto
 */
public final class OntologyUpdated implements DomainEvent
{
	private final ChatSegment segment;


	public OntologyUpdated(ChatSegment segment)
	{
		Validate.notNull(segment, "Segment is null.");
		this.segment = segment;
	}


	public ChatSegment getSegment()
	{
		return segment;
	}

}
