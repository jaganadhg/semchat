package sk.hasto.semchat.application.eventhandlers;

import sk.hasto.semchat.domain.DomainEventBus;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.events.MessageRejected;
import sk.hasto.semchat.domain.DomainEventHandler;
import sk.hasto.semchat.domain.events.SegmentsUpdated;

/**
 * @author Branislav Hasto
 */
public final class MessageRejectedHandler implements DomainEventHandler<MessageRejected>
{

	public void handle(MessageRejected event)
	{
		ChatSegment newSegment = new ChatSegment();
		newSegment.addMessage(event.getMessage());
		DomainEventBus.publish(new SegmentsUpdated());
	}
	
}
