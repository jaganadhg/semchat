package sk.hasto.semchat.domain.model;

import java.util.Collections;
import java.util.Date;
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
	private final Set<OntologyAnnotation> annotations;

	
	/**
	 * Vytvori novu spravu.
	 * @param text text spravy
	 * @param user autor spravy
	 * @throws IllegalArgumentException ak je text null, alebo prazdny
	 */
	public Message(String text, User user, Set<OntologyAnnotation> annotations)
	{
		Validate.notEmpty(text, "Text is null or empty.");
		Validate.notNull(user, "User is null.");
		Validate.notNull(annotations, "Annotation set is null.");

		this.user = user;
		this.text = text.trim();
		this.annotations = annotations;
		this.time = new Date();
	}

	
	/**
	 * @return text spravy
	 */
	public String getText()
	{
		return text;
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
	 * @return anotacie pre tuto spravu
	 */
	Set<OntologyAnnotation> getAnnotations()
	{
		return Collections.unmodifiableSet(annotations);
	}


	/**
	 * Zisti, ci ma tato sprava semanticku hodnotu.
	 * Semanticku hodnotu ma, ak obsahuje vyrazy z ontologie.
	 * @return
	 */
	boolean hasSemanticValue()
	{
		return !annotations.isEmpty();
	}


	@Override
	public String toString()
	{
		return text;
	}

}
