package acceptance;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import gate.Factory;
import gate.creole.gazetteer.Gazetteer;
import gate.creole.ontology.Ontology;
import gate.util.GateException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.ChatSegmentOntology;
import sk.hasto.semchat.domain.model.Message;
import sk.hasto.semchat.domain.model.OntologyAnnotation;
import sk.hasto.semchat.domain.model.User;
import sk.hasto.semchat.domain.services.Annotator;
import sk.hasto.semchat.domain.services.OntologyGenerator;
import sk.hasto.semchat.infrastructure.GateInitializer;
import sk.hasto.semchat.infrastructure.GateResourceFactory;
import sk.hasto.semchat.infrastructure.services.gate.AnnotatorImpl;
import sk.hasto.semchat.infrastructure.services.gate.SimpleOntologyGenerator;

/**
 * Sluzi na generovanie databazy chat segmentov.
 * Databaza sa generuje z adresara s textami.
 * @author Branislav Hasto
 */
final class SegmentsDbGenerator
{
	private static final Logger logger = Logger.getLogger(SegmentsDbGenerator.class.getName());

	private final Annotator annotator;
	private final OntologyGenerator generator;

	// tieto instancie udrzujeme, aby sme ich mohli neskor uvolnit
	private final Ontology ontology;
	private final Gazetteer gazetteer;

	private ObjectContainer db;


	SegmentsDbGenerator(File webInfDir) throws GateException
	{
		Validate.notNull(webInfDir, "WEB-INF directory path is null.");

		try {
			// init processing resources
			GateInitializer.init(new File(webInfDir, "gate"));
			GateResourceFactory factory = new GateResourceFactory();
			File ontologiesDir = new File(webInfDir, "data/ontologies");
			URL ontologyUrl = new File(ontologiesDir, "astronomy.xml").toURI().toURL();
			URL mappingsUrl = new File(ontologiesDir, "mappings.txt").toURI().toURL();
			ontology = factory.newOntology(ontologyUrl, mappingsUrl);
			gazetteer = factory.newOntologyBasedGazetteer(ontology);
			annotator = new AnnotatorImpl(gazetteer);
			generator = new SimpleOntologyGenerator(ontology);
		}
		catch (MalformedURLException ex) {
			throw new AssertionError(ex);
		}

	}


	/**
	 * Free resources.
	 */
	void free()
	{
		if (ontology != null) {
			Factory.deleteResource(ontology);
		}

		if (gazetteer != null) {
			Factory.deleteResource(gazetteer);
		}
	}


	/**
	 * Do daneho suboru zapise novu databazy.
	 * Naplni ju segmentmi vytvorenymi z textov v zadanom adresari.
	 * @param dbFile subor s databazou
	 * @param textsDir adresar s textami
	 * @throws GateException
	 * @throws IOException
	 */
	void generate(File dbFile, File textsDir) throws IOException
	{
		Validate.notNull(dbFile, "Database file is null.");
		Validate.notNull(textsDir, "Texts directory is null.");

		try {
			dbFile.delete();
			db = Db4oEmbedded.openFile(dbFile.getPath());
			processAllFiles(textsDir);
		}
		finally {
			if (db != null) {
				db.close();
			}
		}
	}


	private void processAllFiles(File file) throws IOException
	{
		logger.info("File: " + file.getPath());
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				processAllFiles(new File(file, children[i]));
			}
		} else {
			processFile(file);
		}
	}


	private void processFile(File file) throws IOException
	{
		// kazdy clanok bude iba jedna sprava a bude vo vlastnom segmente
		// kedze clanky su zaradene v adresaroch podla kategorii a tieto kategorie
		// si chceme niekam ulozit kvoli vysledkom testov, tak ich ulozime ako odosielatela
		String text = FileUtils.readFileToString(file);
		User user = new User(new File(file.getParent()).getName());
		Set<OntologyAnnotation> annotations = annotator.annotate(text);
		Message message = new Message(text, user, annotations);

		ChatSegment segment = new ChatSegment();
		segment.addMessage(message);
		ChatSegmentOntology generatedOntology = generator.generate(segment);
		segment.updateOntology(generatedOntology);
		db.store(segment);
	}

}
