package sk.hasto.semchat.infrastructure.services.gate;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.clone.ql.OntoRootGaz;
import gate.creole.POSTagger;
import gate.creole.ResourceInstantiationException;
import gate.creole.morph.Morph;
import gate.creole.ontology.Ontology;
import gate.creole.tokeniser.DefaultTokeniser;
import org.apache.commons.lang.Validate;


/**
 * Factory for Gate resources.
 * @author Branislav Hasto
 */
public final class GateResourceFactory
{

	/**
	 * Vytvori prazdny GATE dokument.
	 * @return prazdny dokument
	 * @throws ResourceInstantiationException
	 */
	public Document newEmptyDocument() throws ResourceInstantiationException
	{
		return Factory.newDocument("");
	}


	/**
	 * Vytvori GATE dokument z nastaveneho retazca.
	 * @param content
	 * @return dokument so zadanym obsahom
	 * @throws ResourceInstantiationException
	 */
	public Document newDocument(String content) throws ResourceInstantiationException
	{
		Validate.notNull(content, "Document content must not be null.");
		return Factory.newDocument(content);
	}


	/**
	 * Vytvori gazetteer zalozeny na ontologii.
	 * @param ontology
	 * @return
	 * @throws ResourceInstantiationException
	 */
	public OntoRootGaz newOntologyBasedGazetteer(Ontology ontology)
			throws ResourceInstantiationException
	{
		Validate.notNull(ontology, "Ontology must not be null.");

		FeatureMap params = Factory.newFeatureMap();
		params.put("tokeniser", newTokeniser());
		params.put("posTagger", newPosTagger());
		params.put("morpher", newMorpher());
		params.put("ontology", ontology);

		return (OntoRootGaz) Factory.createResource(OntoRootGaz.class.getName(), params);
	}


	/**
	 * @return new morpher with default settings
	 * @throws ResourceInstantiationException
	 */
	private Morph newMorpher() throws ResourceInstantiationException
	{
		return (Morph) Factory.createResource(Morph.class.getName());
	}


	/**
	 * @return new pos tagger with default settings
	 * @throws ResourceInstantiationException
	 */
	private POSTagger newPosTagger() throws ResourceInstantiationException
	{
		return (POSTagger) Factory.createResource(POSTagger.class.getName());
	}


	/**
	 * @return new tokeniser with default settings
	 * @throws ResourceInstantiationException
	 */
	private DefaultTokeniser newTokeniser() throws ResourceInstantiationException
	{
		return (DefaultTokeniser) Factory.createResource(DefaultTokeniser.class.getName());
	}

}
