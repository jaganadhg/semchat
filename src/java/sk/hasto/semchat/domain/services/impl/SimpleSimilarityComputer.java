package sk.hasto.semchat.domain.services.impl;

import gate.creole.ontology.OURI;
import java.util.Set;
import org.apache.commons.lang.Validate;
import sk.hasto.java.collections.SetUtils;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Similarity;
import sk.hasto.semchat.domain.services.SimilarityComputer;

/**
 * Urcuje podobnost podla poctu konceptov.
 * @author Branislav Hasto
 */
public final class SimpleSimilarityComputer implements SimilarityComputer
{

	public Similarity compute(ChatSegment source, ChatSegment target)
	{
		Validate.notNull("Source segment must not be null.");
		Validate.notNull("Target segment must not be null.");

		Set<OURI> classes1 = source.getOntology().getClasses();
		Set<OURI> classes2 = target.getOntology().getClasses();
		Set<OURI> instances1 = source.getOntology().getInstances();
		Set<OURI> instances2 = target.getOntology().getInstances();

		Set<OURI> classesUnion = SetUtils.union(classes1, classes2);
		Set<OURI> instancesUnion = SetUtils.union(instances1, instances2);
		Set<OURI> classesJoin = SetUtils.join(classes1, classes2);
		Set<OURI> instancesJoin = SetUtils.join(instances1, instances2);

		Set<OURI> join = SetUtils.union(classesJoin, instancesJoin);
		Set<OURI> union = SetUtils.union(classesUnion, instancesUnion);
		
		float similarity = join.size() / (float) union.size();

		return new Similarity(source, target, similarity, classesJoin, instancesJoin);
	}

}
