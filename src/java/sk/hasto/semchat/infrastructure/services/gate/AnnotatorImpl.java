package sk.hasto.semchat.infrastructure.services.gate;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.creole.gazetteer.Gazetteer;
import gate.creole.ontology.OURI;
import gate.creole.ontology.impl.sesame.OURIImpl;
import gate.util.GateException;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.Validate;
import sk.hasto.semchat.domain.model.ChatSegmentOntology.ConceptType;
import sk.hasto.semchat.domain.model.Message;
import sk.hasto.semchat.domain.model.OntologyAnnotation;
import sk.hasto.semchat.domain.services.AnnotatorException;
import sk.hasto.semchat.domain.services.Annotator;

/**
 * @author Branislav Hasto
 */
public final class AnnotatorImpl implements Annotator
{
	private final Gazetteer gazetteer;
	private final GateResourceFactory factory = new GateResourceFactory();


	public AnnotatorImpl(Gazetteer gazetteer)
	{
		Validate.notNull(gazetteer, "Gazetteer must not be null.");
		this.gazetteer = gazetteer;
	}


	public void annotate(Message message) throws AnnotatorException
	{
		Validate.notNull(message, "Message must not be null.");

		Document document = null;
		try {
			document = factory.newDocument(message.getText());
			gazetteer.setDocument(document);
			gazetteer.execute();

			AnnotationSet gateAnnotations = document.getAnnotations();
			Set<OntologyAnnotation> annotations = new HashSet<OntologyAnnotation>();
			for (Annotation gateAnnotation : gateAnnotations) {
				OntologyAnnotation annotation = convertGateAnnotation(gateAnnotation);
				if (annotation != null) {
					annotations.add(annotation);
				}
			}

			message.setAnnotations(annotations);
		}

		catch (GateException ex) {
			throw new AnnotatorException(ex);
		}

		finally {
			if (document != null) {
				Factory.deleteResource(document);
			}
		}
	}


	/**
	 * Skonvertuje GATE anotaciu na nasu domenovu anotaciu.
	 * @param annotation
	 * @return
	 */
	private OntologyAnnotation convertGateAnnotation(Annotation annotation)
	{
		String uriString = (String) annotation.getFeatures().get("URI");
		String type = (String) annotation.getFeatures().get("type");
		OURI uri = new OURIImpl(uriString);

		if ("instance".equals(type)) {
			return new OntologyAnnotation(uri, ConceptType.INSTANCE);
		} else if ("class".equals(type)) {
			return new OntologyAnnotation(uri, ConceptType.CLASS);
		} else {
			return null;
		}
	}

}
