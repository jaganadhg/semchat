package sk.hasto.semchat.domain.services.repositories;

import java.util.List;
import sk.hasto.semchat.domain.common.Repository;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Similarity;

/**
 * Ulozisko segmentov.
 * @author Branislav Hasto
 */
public interface ChatSegmentRepository extends Repository<ChatSegment>
{

	/**
	 * @return najnovsi (aktualny) segment
	 */
	ChatSegment getLast();


	/**
	 * Vrati vysledky hladania podobnych segmentov.
	 * Vysledky su zoradene podla miery podobnosti zostupne.
	 * Vo vysledkoch su len tie, kde podobnost so zadanym
	 * segmentom je vacsia alebo rovna ako zadana minimalna podobnost.
	 * @param segment segment, pre ktory hladame podobne segmenty
	 * @param minSimilarity minimalna pozadovana podobnost
	 * @return zoradene vysledky hladania
	 */
	List<Similarity> getSimilarSegments(ChatSegment segment,
			                            float minSimilarity);


	/**
	 * Ulozi segment.
	 * @param updatedSegment
	 */
	void store(ChatSegment segment);


	/**
	 * Vrati segment podla identifikatora.
	 * Ak sa segment nenajde, mal by vratit null.
	 * @param id id segmentu
	 * @return segment so zadanym id, alebo null,
	 * ak segment so zadanym id nie je v databaze
	 */
	public ChatSegment getById(long id);

}
