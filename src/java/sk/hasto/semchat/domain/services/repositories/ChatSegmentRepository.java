package sk.hasto.semchat.domain.services.repositories;

import sk.hasto.semchat.domain.common.Repository;
import java.util.LinkedHashMap;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Similarity;

/**
 * Ulozisko segmentov.
 * @author Branislav Hasto
 */
public interface ChatSegmentRepository extends Repository<ChatSegment>
{

	/**
	 * Vrati posledny segment.
	 * @return
	 */
	ChatSegment getLast();


	/**
	 * Vrati mapu s polozkami (segment, podobnost), kde podobnost
	 * je vacsia alebo rovnaka ako minimalna pozadovana podobnost.
	 * @param segment segment, pre ktory hladame podobne segmenty
	 * @param minSimilarity minimalna pozadovana podobnost
	 * @return
	 */
	LinkedHashMap<ChatSegment, Similarity> getSimilarSegments(ChatSegment segment,
															  Similarity minSimilarity);


	/**
	 * Ulozi segment.
	 * @param updatedSegment
	 */
	void store(ChatSegment segment);

}
