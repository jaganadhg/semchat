package sk.hasto.semchat.application.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sk.hasto.semchat.application.ChatWriteService;
import org.apache.commons.lang.Validate;
import sk.hasto.semchat.application.ChatReadService;
import sk.hasto.semchat.domain.common.Events;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Message;
import sk.hasto.semchat.domain.model.MessageAddedEvent;
import sk.hasto.semchat.domain.model.Similarity;
import sk.hasto.semchat.domain.model.User;
import sk.hasto.semchat.domain.services.ChatSegmenterException;
import sk.hasto.semchat.domain.services.repositories.ChatSegmentRepository;
import sk.hasto.semchat.domain.services.repositories.MessageRepository;
import sk.hasto.semchat.domain.services.OntologyGenerator;
import sk.hasto.semchat.domain.services.ChatSegmenter;

/**
 * Implementuje sluzby chatu.
 * @author Branislav Hasto
 */
public class ChatServiceImpl implements ChatReadService, ChatWriteService
{
	/** pocet zobrazovanych sprav */
	private static final int MESSAGES_COUNT = 30;

	/** minimalne podobnost pri hladani podobnych segmentov */
	private static final float MIN_SIMILARITY = .0f;

	private static final Logger logger = Logger.getLogger(ChatServiceImpl.class.getName());
	private final ChatSegmenter chatSegmenter;
	private final OntologyGenerator ontologyGenerator;
	private final ChatSegmentRepository segmentRepository;
	private final MessageRepository messageRepository;


	public ChatServiceImpl(ChatSegmenter chatSegmenter, OntologyGenerator ontologyGenerator,
			ChatSegmentRepository segmentRepository, MessageRepository messageRepository)
	{
		Validate.notNull(chatSegmenter, "Chat segmenter must not be null.");
		Validate.notNull(ontologyGenerator, "Ontology generator must not be null.");
		Validate.notNull(segmentRepository, "Segment repository must not be null.");
		Validate.notNull(messageRepository, "Message repository must not be null.");

		this.chatSegmenter = chatSegmenter;
		this.ontologyGenerator = ontologyGenerator;
		this.segmentRepository = segmentRepository;
		this.messageRepository = messageRepository;
	}


	public void addMessage(String text, User user)
	{
		Validate.notEmpty(text, "Text must not be null or empty.");
		Validate.notNull(user, "User must not be null.");

		Message message = new Message(text, user);
		messageRepository.store(message);
		Events.raise(new MessageAddedEvent(message));
		updateSegments(message);
	}


	public List<Message> getLastMessages()
	{
		return messageRepository.getLast(MESSAGES_COUNT);
	}


	public LinkedHashMap<ChatSegment, Similarity> getSegmentsSimilarToCurrent()
	{
		ChatSegment lastSegment = segmentRepository.getLast();

		logger.fine("Current segment: " + lastSegment);

		Similarity minSimilarity = new Similarity(MIN_SIMILARITY);
		return segmentRepository.getSimilarSegments(lastSegment, minSimilarity);
	}


	/**
	 * Updatne segmenty na zaklade prijatej spravy.
	 * @param message
	 */
	private void updateSegments(Message message)
	{
		try {
			ChatSegment lastSegment = segmentRepository.getLast();
			ChatSegment newSegment = chatSegmenter.determineSegment(message, lastSegment);
			newSegment.addMessage(message);
			ontologyGenerator.generate(newSegment);
			segmentRepository.store(newSegment);
		}

		catch (ChatSegmenterException ex) {
			logger.log(Level.SEVERE, "Segment couldn't be determined.", ex);
		}
	}

}