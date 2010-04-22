package sk.hasto.semchat.domain.services;

import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Similarity;

/**
 * Sluzi na urcovanie podobnosti medzi segmentmi.
 * @author Branislav Hasto
 */
public interface SimilarityMeasurer
{

	/**
	 * @param source
	 * @param target
	 * @return podobnost segmentov
	 */
	Similarity measure(ChatSegment source, ChatSegment target);

}
