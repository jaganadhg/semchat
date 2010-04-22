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


	/**
	 * @return mnozina URI tried v ontologii
	 */
	public Set<OURI> getClasses()
	{
		return Collections.unmodifiableSet(classes);
	}


	/**
	 * Prida triedu do ontologie.
	 * @param uri uri triedy
	 */
	public void addClass(OURI uri)
	{
		Validate.notNull(uri, "URI must not be null.");
		classes.add(uri);
	}


	@Override
	public String toString()
	{
		return classes.toString();
	}

}
