package sk.hasto.semchat.infrastructure;

import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.clone.ql.OntoRootGaz;
import gate.creole.POSTagger;
import gate.creole.ResourceInstantiationException;
import gate.creole.morph.Morph;
import gate.creole.ontology.Ontology;
import gate.creole.ontology.impl.sesame.OWLIMOntology;
import gate.creole.tokeniser.DefaultTokeniser;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
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
		Validate.notNull(content, "Document content is null.");
		return Factory.newDocument(content);
	}


	/**
	 * Vytvori novu ontologiu zo zadaneho URL.
	 * @param path URL ontologie
	 * @return ontologia
	 * @throws ResourceInstantiationException
	 */
	public Ontology newOntology(URL url) throws ResourceInstantiationException
	{
		Validate.notNull(url, "Ontology url is null.");
		FeatureMap params = Factory.newFeatureMap();
		params.put("rdfXmlURL", url);

		return (Ontology) Factory.createResource(OWLIMOntology.class.getName(), params);
	}


	/**
	 * 
	 * @param url
	 * @param mappingsFile subor s mapovanim remote URL na lokalne subort
	 * @return
	 * @throws ResourceInstantiationException
	 */
	public Ontology newOntology(URL url, URL mappingsFile) throws ResourceInstantiationException
	{
		Validate.notNull(url, "Ontology url is null.");
		Validate.notNull(mappingsFile, "Mapping file url is null.");
		FeatureMap params = Factory.newFeatureMap();
		params.put("rdfXmlURL", url);
		params.put("mappingsURL", mappingsFile);

		return (Ontology) Factory.createResource(OWLIMOntology.class.getName(), params);
	}



	/**
	 * Vytvori novu ontologiu zo zadaneho suboru.
	 * @param path cesta k ontologii
	 * @return ontologia
	 * @throws ResourceInstantiationException
	 */
	public Ontology newOntology(File path) throws ResourceInstantiationException
	{
		try {
			Validate.notNull(path, "Ontology path is null.");
			return newOntology(path.toURI().toURL());
		}
		catch (MalformedURLException ex) {
			// url creation under control
			throw new AssertionError(ex);
		}
	}


	/**
	 * Vytvori gazetteer zalozeny na zadanej ontologii.
	 * Slova, ktore bude gazetteer vyhladavat, budu pojmy z ontologie.
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
