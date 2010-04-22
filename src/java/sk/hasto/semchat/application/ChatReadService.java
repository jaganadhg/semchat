package sk.hasto.semchat.application;

import java.util.LinkedHashMap;
import java.util.List;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Message;
import sk.hasto.semchat.domain.model.Similarity;

/**
 * Chat sluzba umoznujuca ziskavanie udajov.
 * @author Branislav Hasto
 */
public interface ChatReadService
{

	/**
	 * @return posledne spravy z chatu
	 */
	List<Message> getLastMessages();


	/**
	 * @return segmenty podobne aktualne otvorenemu segmentu
	 */
	LinkedHashMap<ChatSegment, Similarity> getSegmentsSimilarToCurrent();
}
