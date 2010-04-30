package sk.hasto.semchat.domain.services;

import java.util.Set;
import sk.hasto.semchat.domain.model.OntologyAnnotation;

/**
 * Zabezpecuje anotovanie textov.
 * Texty musia byt najprv anotovane, aby sa dali semanticky spracovat.
 * @author Branislav Hasto
 */
public interface Annotator
{
	/**
	 * Vykona anotovanie.
	 * @param text text na anotovanie
	 */
	Set<OntologyAnnotation> annotate(String text);
}
