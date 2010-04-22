package sk.hasto.semchat.domain.services;

import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.ChatSegmentOntology;

/**
 * Zabezpecuje tvorbu ontologie z chatovych segmentov.
 * @author Branislav Hasto
 */
public interface OntologyGenerator
{

	/**
	 * Vygeneruje ontologiu na zaklade anotacii.
	 * @param unit
	 * @return
	 */
	ChatSegmentOntology generate(ChatSegment segment);

}
