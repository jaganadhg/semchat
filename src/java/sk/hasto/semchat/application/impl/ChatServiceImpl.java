package sk.hasto.semchat.application.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang.Validate;
import sk.hasto.semchat.application.ChatService;
import sk.hasto.semchat.domain.DomainEventBus;
import sk.hasto.semchat.domain.events.MessagesUpdated;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Message;
import sk.hasto.semchat.domain.model.OntologyAnnotation;
import sk.hasto.semchat.domain.model.Similarity;
import sk.hasto.semchat.domain.model.User;
import sk.hasto.semchat.domain.services.Annotator;
import sk.hasto.semchat.domain.services.repositories.ChatSegmentRepository;
import sk.hasto.semchat.domain.services.repositories.MessageRepository;

/**
 * Implementuje sluzby chatu.
 * @author Branislav Hasto
 */
public class ChatServiceImpl implements ChatService
{
	/** minimalna podobnost pri hladani podobnych segmentov */
	private final float minSimilarity;

	/** pocet zobrazenych sprav v chate */
	private final int messageCount;

	private final Annotator annotator;
	private final MessageRepository messageRepository;
	private final ChatSegmentRepository segmentRepository;


	public ChatServiceImpl(Annotator annotator, MessageRepository messageRepository,
						   ChatSegmentRepository segmentRepository, float minSimilarity,
						   int messageCount)
	{
		Validate.notNull(annotator, "Annotator is null.");
		Validate.notNull(messageRepository, "Message repository is null.");
		Validate.notNull(segmentRepository, "Segment repository is null.");
		Validate.isTrue(messageCount > 0, "Message count must be greater than 0.");

		this.annotator = annotator;
		this.messageRepository = messageRepository;
		this.segmentRepository = segmentRepository;
		this.minSimilarity = minSimilarity;
		this.messageCount = messageCount;
	}


	public void addMessage(String text, User user)
	{
		Validate.notEmpty(text, "Text is null or empty.");
		Validate.notNull(user, "User is null.");

		Set<OntologyAnnotation> annotations = annotator.annotate(text);
		Message message = new Message(text, user, annotations);
		messageRepository.store(message);
		DomainEventBus.publish(new MessagesUpdated());

		ChatSegment currentSegment = segmentRepository.getLast();
		if (currentSegment == null) {
			currentSegment = new ChatSegment();
		}
		currentSegment.addMessage(message);
	}


	public List<Message> getLastMessages()
	{
		return messageRepository.getLast(messageCount);
	}


	public List<Similarity> getSegmentsSimilarToCurrent()
	{
		ChatSegment current = segmentRepository.getLast();
		return segmentRepository.findSimilarSegments(current, minSimilarity);
	}


	public ChatSegment getSegment(UUID id)
	{
		return segmentRepository.getById(id);
	}


	public ChatSegment getCurrentSegment()
	{
		return segmentRepository.getLast();
	}

}
