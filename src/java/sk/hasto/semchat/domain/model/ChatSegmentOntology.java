package sk.hasto.semchat.domain.model;

import gate.creole.ontology.OURI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.Validate;

/**
 * Ontologia pre segment chatu.
 * @author Branislav Hasto
 */
public final class ChatSegmentOntology
{

	/**
	 * Typ ontologickeho konceptu zachyteneho anotaciou.
	 * V aplikacii sa anotuju 2 typy konceptov, triedy a instancie.
	 */
	public enum ConceptType {
		CLASS, INSTANCE
	}


	/** URIs tried v ontologii */
	private final Set<OURI> classes = new HashSet<OURI>();

	/** URIs instancii v ontologii */
	private final Set<OURI> instances = new HashSet<OURI>();


	/**
	 * @return mnozina URIs vsetkych tried v ontologii
	 */
	public Set<OURI> getClasses()
	{
		return Collections.unmodifiableSet(classes);
	}


	/**
	 * @return mnozina URIs vsetkych instancii v ontologii
	 */
	public Set<OURI> getInstances()
	{
		return Collections.unmodifiableSet(instances);
	}


	/**
	 * Prida triedu do ontologie.
	 * @param uri uri triedy
	 */
	public void addClass(OURI uri)
	{
		Validate.notNull(uri, "Uri is null.");
		classes.add(uri);
	}

	
	/**
	 * Prida instanciu do onotlogie.
	 * @param uri uri instancie
	 */
	public void addInstance(OURI uri)
	{
		Validate.notNull(uri, "Uri is null.");
		instances.add(uri);
	}


	/**
	 * Updates this ontology with data from other ontology.
	 * @param other
	 */
	public void updateWith(ChatSegmentOntology other)
	{
		Validate.notNull(other, "Other ontology is null.");
		for (OURI classUri : other.classes) {
			classes.add(classUri);
		}
		for (OURI instanceUri : other.instances) {
			instances.add(instanceUri);
		}
	}


	@Override
	public String toString()
	{
		return "Classes: " + classes + "\nInstances: " + instances;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final ChatSegmentOntology other = (ChatSegmentOntology) obj;
		if (classes != other.classes && (!classes.equals(other.classes))) {
			return false;
		}
		if (instances != other.instances && (!instances.equals(other.instances))) {
			return false;
		}
		return true;
	}


	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 37 * hash + classes.hashCode();
		hash = 37 * hash + instances.hashCode();
		return hash;
	}

}
