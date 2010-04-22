package sk.hasto.semchat.domain.services.impl;

import gate.creole.ontology.OURI;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;
import sk.hasto.java.collections.SetUtils;
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


	public Similarity measure(ChatSegment source, ChatSegment target)
	{
		logger.finest("Segment1: " + source + "\nSegment2: " + target);
		Validate.notNull("Source segment must not be null.");
		Validate.notNull("Target segment must not be null.");
		Validate.isTrue(source.hasOntology(), "Source segment has no ontology assigned.");
		Validate.isTrue(target.hasOntology(), "Target segment has no ontology assigned.");

		Set<OURI> classes1 = source.getOntology().getClasses();
		Set<OURI> classes2 = target.getOntology().getClasses();

		Set<OURI> join = SetUtils.join(classes1, classes2);
		Set<OURI> union = SetUtils.union(classes1, classes2);
		
		float similarity = join.size() / (float) union.size();
		logger.fine("Computed similarity: " + similarity);

		return new Similarity(source, target, similarity, join);
	}

}
