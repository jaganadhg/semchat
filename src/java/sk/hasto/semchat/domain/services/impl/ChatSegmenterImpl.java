package sk.hasto.semchat.domain.services.impl;

import org.apache.commons.lang.Validate;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Message;
import sk.hasto.semchat.domain.services.Annotator;
import sk.hasto.semchat.domain.services.AnnotatorException;
import sk.hasto.semchat.domain.services.ChatSegmenter;
import sk.hasto.semchat.domain.services.ChatSegmenterException;

/**
 * @author Branislav Hasto
 */
public class ChatSegmenterImpl implements ChatSegmenter
{
	private final Annotator annotator;


	public ChatSegmenterImpl(Annotator annotator)
	{
		Validate.notNull(annotator, "Annotator must not be null.");
		this.annotator = annotator;
	}


	public ChatSegment determineSegment(Message message, ChatSegment lastSegment)
			throws ChatSegmenterException
	{
		Validate.notNull(message, "Message must not be null.");

		try {
			annotator.annotate(message);
			return lastSegment != null && lastSegment.accepts(message)
				   ? lastSegment : new ChatSegment();
		}

		catch (AnnotatorException ex) {
			throw new ChatSegmenterException(ex);
		}
	}
}
