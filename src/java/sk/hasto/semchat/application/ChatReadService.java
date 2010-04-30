package sk.hasto.semchat.application;

import java.util.List;
import java.util.UUID;
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
	 * @param id id segmentu
	 * @return segment so zadanym id, alebo null, ak sa segment nenajde
	 */
	ChatSegment getSegment(UUID id);


	/**
	 * @return aktualny otvoreny segment
	 */
	ChatSegment getCurrentSegment();


	/**
	 * @return segmenty podobne aktualne otvorenemu segmentu
	 */
	List<Similarity> getSegmentsSimilarToCurrent();
	
}
