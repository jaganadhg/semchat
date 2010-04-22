package sk.hasto.semchat.infrastructure.services.gate;

import gate.creole.ontology.OClass;
import gate.creole.ontology.OConstants.Closure;
import gate.creole.ontology.OInstance;
import gate.creole.ontology.ONodeID;
import gate.creole.ontology.OURI;
import gate.creole.ontology.Ontology;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.ChatSegmentOntology;
import sk.hasto.semchat.domain.model.ChatSegmentOntology.ConceptType;
import sk.hasto.semchat.domain.model.OntologyAnnotation;
import sk.hasto.semchat.domain.services.OntologyGenerator;

/**
 * Velmi jednoduchy generator ontologii z anotovaneho textu.
 * @author Branislav Hasto
 */
public final class SimpleOntologyGenerator implements OntologyGenerator
{
	private static final Logger logger = Logger.getLogger(SimpleOntologyGenerator.class.getName());

	/** ontologie, z ktorej sa generuju podontologie */
	private final Ontology ontology;


	public SimpleOntologyGenerator(Ontology ontology)
	{
		Validate.notNull(ontology, "Ontology must not be null.");
		this.ontology = ontology;
	}


	public ChatSegmentOntology generate(ChatSegment segment)
	{
		logger.finest("Segment: " + segment.toString());
		Validate.notNull(segment, "Segment must not be null.");

		ChatSegmentOntology generated
				= segment.hasOntology() ? segment.getOntology() : new ChatSegmentOntology();

		// prida triedy zo segmentovych anotacii a rozvinie ich
		Set<OURI> classUris = getOClassUrisFromSegment(segment);
		for (OURI uri : classUris) {
			generated.addClass(uri);
			addClassesToOntology(generated, getAllSuperClasses(uri));
		}

		// prida instancie zo segmentovych anotacii a rozvinie ich
		Set<OURI> instanceUris = getInstanceUrisFromSegment(segment);
		for (OURI uri : instanceUris) {
			addClassesToOntology(generated, getAllSuperClasses(uri));
		}

		logger.fine("Generated ontology: " + generated.toString());

		segment.setOntology(generated);
		return generated;
	}


	/**
	 * Vrati rodicov zadaneho OResource z povodnej ontologie.
	 * @param uri uri OResource, pre ktory hladame rodicov
	 * @return rodicovske triedy zadaneho OResource
	 */
	private Set<OClass> getAllSuperClasses(OURI uri)
	{
		// uri by malo byt jedine uri triedy alebo instancie
		// uri ziadnych inych konceptov (napr. relacie) by sa sem dostat nemalo
		assert ontology.containsOInstance(uri) || ontology.containsOClass(uri);

		if (ontology.containsOInstance(uri)) {
			OInstance originalInstance = ontology.getOInstance(uri);
			return originalInstance.getOClasses(Closure.TRANSITIVE_CLOSURE);
		}

		else {
			OClass originalClass = ontology.getOClass(uri);
			return originalClass.getSuperClasses(Closure.TRANSITIVE_CLOSURE);
		}
	}


	/**
	 * Prida zadane triedy do ontologie.
	 * @param ontology
	 * @param classes
	 */
	private void addClassesToOntology(ChatSegmentOntology ontology, Set<OClass> classes)
	{
		for (OClass oClass : classes) {
			ONodeID nodeId = oClass.getONodeID();
			// pridame iba OURI (lebo triedy mozu byt aj anonymne, tie nechceme)
			if (nodeId instanceof OURI) {
				ontology.addClass((OURI) nodeId);
			}
		}
	}


	/**
	 * Vrati mnozinu instancii, ktore sa vyskytuju v segmente chatu.
	 * @param document
	 * @return
	 */
	private Set<OURI> getInstanceUrisFromSegment(ChatSegment segment)
	{
		return getConceptUrisFromSegment(segment, ConceptType.INSTANCE);
	}


	/**
	 * Vrati mnozinu tried, ktore sa vyskytuju v segmente chatu.
	 * @param document
	 * @return
	 */
	private Set<OURI> getOClassUrisFromSegment(ChatSegment segment)
	{
		return getConceptUrisFromSegment(segment, ConceptType.CLASS);
	}


	/**
	 * Vrati mnozinu OResource zadaneho typu, ktore sa vyskytuju v segmente chatu.
	 * @param segment
	 * @param type trieda alebo instancia
	 * @return
	 */
	private Set<OURI> getConceptUrisFromSegment(ChatSegment segment, ConceptType type)
	{
		Set<OURI> uris = new HashSet<OURI>(segment.getAnnotations().size());
		for(OntologyAnnotation annotation : segment.getAnnotations()) {
			if (annotation.getType() == type) {
				uris.add(annotation.getUri());
			}
		}

		return uris;
	}

}
