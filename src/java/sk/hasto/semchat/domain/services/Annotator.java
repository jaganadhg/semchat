package sk.hasto.semchat.domain.services;

import sk.hasto.semchat.domain.model.Message;

/**
 * Zabezpecuje anotovanie sprav.
 * Sprava musi byt najprv anotovana, aby sa dala semanticky spracovat.
 * @author Branislav Hasto
 */
public interface Annotator
{
	/**
	 * Vykona anotovanie. Sprava bude po vykonani v anotovanom stave.
	 * @param message sprava na anotovanie
	 * @throws AnnotationException ak sa vyskytne problem pri anotovani
	 */
	void annotate(Message message) throws AnnotatorException;
}
