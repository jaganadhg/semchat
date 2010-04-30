package sk.hasto.semchat.application.eventhandlers;

import org.apache.commons.lang.Validate;
import sk.hasto.semchat.domain.DomainEventHandler;
import sk.hasto.semchat.domain.events.MessageAdded;
import sk.hasto.semchat.domain.services.repositories.ChatSegmentRepository;

/**
 * @author Branislav Hasto
 */
public class MessageAddedHandler<T extends MessageAdded> implements DomainEventHandler<T>
{

	private final ChatSegmentRepository repository;


	public MessageAddedHandler(ChatSegmentRepository repository)
	{
		Validate.notNull(repository, "Repository is null.");
		this.repository = repository;
	}

	
	public void handle(T event)
	{
		repository.store(event.getSegment());
	}

}
