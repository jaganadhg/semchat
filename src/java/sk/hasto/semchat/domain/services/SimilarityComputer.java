package sk.hasto.semchat.domain.services;

import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Similarity;

/**
 * Pocita podobnosti medzi segmentmi.
 * @author Branislav Hasto
 */
public interface SimilarityComputer
{

	/**
	 * Vypocita podobnost.
	 * Pre podrobny opis vysledku vid {@link Similarity}.
	 * @param source zdrojovy segmentt (original)
	 * @param target cielovy segment (podobny originalu)
	 * @return podobnost segmentov
	 */
	Similarity compute(ChatSegment source, ChatSegment target);

}
