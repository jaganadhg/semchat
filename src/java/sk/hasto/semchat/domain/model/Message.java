package sk.hasto.semchat.domain.model;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.Validate;


/**
 * Udaje o sprave.
 * @author Branislav Hasto
 */
public final class Message
{
	/** text spravy */
	private final String text;

	/** cas odoslania */
	private final Date time;

	/** odosielatel spravy */
	private final User user;

	/** anotacie pre tuto spravu */
	private Set<OntologyAnnotation> annotations;

	
	/**
	 * Vytvori novu spravu.
	 * @param text text spravy
	 * @param user autor spravy
	 * @throws IllegalArgumentException ak je text null, alebo prazdny
	 */
	public Message(String text, User user)
	{
		Validate.notEmpty(text, "Text must not be null or empty.");
		Validate.notNull(user, "User must not be null.");

		this.user = user;
		this.text = text.trim();
		time = new Date();
	}

	
	/**
	 * @return text spravy
	 */
	public String getText()
	{
		return this.text;
	}

	
	/**
	 * @return cas odoslania spravy
	 */
	public Date getTime()
	{
		return new Date(time.getTime());
	}


	/**
	 * @return odosielatel spravy
	 */
	public User getUser()
	{
		return user;
	}


	/**
	 * Nastavi anotacie pre tuto spravu.
	 * @param annotations
	 */
	public void setAnnotations(Set<OntologyAnnotation> annotations)
	{
		this.annotations = new HashSet<OntologyAnnotation>(annotations);
	}


	/**
	 * @return anotacie pre tuto spravu
	 */
	Set<OntologyAnnotation> getAnnotations()
	{
		return Collections.unmodifiableSet(annotations);
	}


	/**
	 * Zisti, ci je sprava uz anotovana.
	 * @return
	 */
	public boolean isAnnotated()
	{
		return annotations != null;
	}


	/**
	 * Zisti, ci ma tato sprava semanticku hodnotu.
	 * Semanticku hodnotu ma sprava vtedy, ak sa
	 * v nej nachadzaju vyrazy z ontologie.
	 * @return
	 */
	public boolean hasSemanticValue()
	{
		if (!isAnnotated()) {
			throw new IllegalStateException("Message must be annotated.");
		}
		
		return !annotations.isEmpty();
	}


	@Override
	public String toString()
	{
		return text;
	}

}
