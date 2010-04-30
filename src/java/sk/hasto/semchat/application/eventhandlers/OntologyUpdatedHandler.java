package sk.hasto.semchat.application.eventhandlers;

import java.util.logging.Logger;
import org.apache.commons.lang.Validate;
import sk.hasto.semchat.domain.DomainEventBus;
import sk.hasto.semchat.domain.DomainEventHandler;
import sk.hasto.semchat.domain.events.OntologyUpdated;
import sk.hasto.semchat.domain.events.SegmentsUpdated;
import sk.hasto.semchat.domain.services.repositories.ChatSegmentRepository;

/**
 * @author Branislav Hasto
 */
public final class OntologyUpdatedHandler implements DomainEventHandler<OntologyUpdated>
{
	private final ChatSegmentRepository repository;


	public OntologyUpdatedHandler(ChatSegmentRepository repository)
	{
		Validate.notNull("Repository is null.");
		this.repository = repository;
	}


	public void handle(OntologyUpdated event)
	{
		repository.store(event.getSegment());
		Logger.getAnonymousLogger().info("Updated ontology of " + event.getSegment());

		DomainEventBus.publish(new SegmentsUpdated());
	}

}
