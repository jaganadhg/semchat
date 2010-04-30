package sk.hasto.semchat.application.eventhandlers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.ChatSegmentOntology;
import sk.hasto.semchat.domain.events.SemanticMessageAdded;
import sk.hasto.semchat.domain.services.OntologyGenerator;
import sk.hasto.semchat.domain.services.repositories.ChatSegmentRepository;

/**
 * @author Branislav Hasto
 */
public final class SemanticMessageAddedHandler extends MessageAddedHandler<SemanticMessageAdded>
{
	private final OntologyGenerator generator;
	private final ExecutorService executor = Executors.newSingleThreadExecutor();


	public SemanticMessageAddedHandler(OntologyGenerator generator,
									   ChatSegmentRepository repository)
	{
		super(repository);
		Validate.notNull(generator, "Generator is null.");
		this.generator = generator;
	}


	@Override
	public void handle(final SemanticMessageAdded event)
	{
		Logger.getAnonymousLogger().info("SEMANTIC MESSAGE HANDLER.");
		super.handle(event);
		Logger.getAnonymousLogger().info("Going to execute ontology finder.");
		executor.execute(new Runnable() {
			public void run() {
				Logger.getAnonymousLogger().info("Execution started.");
				ChatSegment segment = event.getSegment();
				ChatSegmentOntology generatedOntology = generator.generate(segment);
				Logger.getAnonymousLogger().info("Generated: " + generatedOntology);
				segment.updateOntology(generatedOntology);
			}
		});
	}

}
