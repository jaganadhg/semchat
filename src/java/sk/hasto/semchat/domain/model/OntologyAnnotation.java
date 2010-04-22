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

	/** urcuje, ci je anotaciou triedy, alebo instancie */
	private final boolean isClassAnnotation;


	/**
	 * @param uri uri konceptu, ktory anotacia zachytava
	 * @parm type typ konceptu, ktory anotacia zachytava
	 */
	public OntologyAnnotation(OURI uri, ChatSegmentOntology.ConceptType type)
	{
		Validate.notNull(uri, "Uri must not be null.");
		Validate.notNull(type, "Type must not be null.");

		this.uri = uri;

		/*
		 * toto prepisanie na boolean je hlavne z dovodu, ze
		 * pouzita objektova databaza ma problemy pri enum typoch
		 * TODO: zistit preco, potom implementovat cistejsie
		 */
		isClassAnnotation = (type == ConceptType.CLASS);
	}


	/**
	 * @return typ konceptu zachyteneho ontologiou
	 */
	public ChatSegmentOntology.ConceptType getType()
	{
		return isClassAnnotation
			   ? ConceptType.CLASS : ConceptType.INSTANCE;
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
		if (uri != other.uri && !uri.equals(other.uri)) {
			return false;
		}

		return isClassAnnotation == other.isClassAnnotation;
	}


	@Override
	public int hashCode()
	{
		int hash = 3;
		hash = 23 * hash + uri.hashCode();
		hash = 23 * hash + (isClassAnnotation ? 1 : 0);
		return hash;
	}


}
