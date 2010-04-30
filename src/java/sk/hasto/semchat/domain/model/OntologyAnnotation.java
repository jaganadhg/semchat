package sk.hasto.semchat.domain.model;

import gate.creole.ontology.OURI;
import org.apache.commons.lang.Validate;
import sk.hasto.semchat.domain.model.ChatSegmentOntology.ConceptType;

/**
 * Anotacia.
 * V aplikacii sa anotacie pouzivaju na zachytenie
 * vyskytu ontologickeho konceptu v texte spravy.
 * @author Branislav Hasto
 */
public final class OntologyAnnotation
{

	/** URI konceptu */
	private final OURI uri;

	/** urcuje typ anotacie (anotacia triedy alebo instancie) */
	private final ConceptType type;


	/**
	 * @param uri uri konceptu, ktory anotacia zachytava
	 * @parm type typ konceptu, ktory anotacia zachytava
	 */
	public OntologyAnnotation(OURI uri, ConceptType type)
	{
		Validate.notNull(uri, "Uri is null.");
		Validate.notNull(type, "Concept type is null.");

		this.uri = uri;
		this.type = type;
	}


	/**
	 * @return typ konceptu zachyteneho ontologiou
	 */
	public ChatSegmentOntology.ConceptType getType()
	{
		return type;
	}


	/**
	 * @return URI konceptu zachyteneho ontologiou
	 */
	public OURI getUri()
	{
		return uri;
	}


	@Override
	public String toString()
	{
		return getType() + " " + uri;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final OntologyAnnotation other = (OntologyAnnotation) obj;
		if (this.uri != other.uri && (this.uri == null || !this.uri.equals(other.uri))) {
			return false;
		}
		return true;
	}


	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 59 * hash + (this.uri != null ? this.uri.hashCode() : 0);
		return hash;
	}

}
