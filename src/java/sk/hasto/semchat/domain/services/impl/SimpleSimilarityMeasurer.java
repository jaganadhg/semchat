package sk.hasto.semchat.domain.services.impl;

import gate.creole.ontology.OURI;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.ChatSegmentOntology;
import sk.hasto.semchat.domain.model.Similarity;
import sk.hasto.semchat.domain.services.SimilarityMeasurer;

/**
 * Urcuje podobnost podla poctu konceptov.
 * @author Branislav Hasto
 */
public final class SimpleSimilarityMeasurer implements SimilarityMeasurer
{
	private static final Logger logger = Logger.getLogger(SimpleSimilarityMeasurer.class.getName());


	public Similarity measure(ChatSegment segment1, ChatSegment segment2)
	{
		logger.finest("Segment1: " + segment1 + "\nSegment2: " + segment2);
		Validate.notNull("First segment must not be null.");
		Validate.notNull("Second segment must not be null.");
		Validate.isTrue(segment1.hasOntology(), "First segment has no ontology assigned.");
		Validate.isTrue(segment2.hasOntology(), "Second segment has no ontology assigned.");

		float similarity = measure(segment1.getOntology(), segment2.getOntology());
		logger.fine("Computed similarity: " + similarity);

		return new Similarity(similarity);
	}


	/**
	 * Vypocita podobnost medzi dvomi segmentovymi ontologiami.
	 * @param ontology1
	 * @param ontology2
	 * @return vypocitana podobnost
	 */
	private float measure(ChatSegmentOntology ontology1, ChatSegmentOntology ontology2)
	{
		if (ontology1 == ontology2) {
			return 1;
		}

		Set<OURI> classes1 = ontology1.getClasses();
		Set<OURI> classes2 = ontology2.getClasses();

		if (classes1.equals(classes2)) {
			return 1;
		}

		Set<OURI> union = new HashSet<OURI>(classes1);
		union.addAll(classes2);

		Set<OURI> join = new HashSet<OURI>(classes1);
		join.retainAll(classes2);

		return join.size() / (float) union.size();
	}



}
