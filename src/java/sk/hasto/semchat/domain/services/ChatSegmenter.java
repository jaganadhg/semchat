package sk.hasto.semchat.domain.services;

import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Message;

/**
 * Sluzba, ktora urcuje segmenty pre jednotlive spravy.
 * @author Branislav Hasto
 */
public interface ChatSegmenter
{

	/**
	 * Urci segment pre spravu.
	 * Urcuje sa na zaklade prijatej spravy a posledneho segmentu.
	 * @param lastSegment
	 * @param message
	 * @return
	 */
	ChatSegment determineSegment(Message message, ChatSegment lastSegment)
			throws ChatSegmenterException;

}
