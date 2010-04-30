package sk.hasto.semchat.infrastructure;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import gate.creole.gazetteer.Gazetteer;
import gate.creole.ontology.OClass;
import gate.creole.ontology.Ontology;
import gate.creole.ontology.impl.sesame.OWLIMOntology;
import gate.util.GateException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import sk.hasto.semchat.application.ChatService;
import sk.hasto.semchat.application.eventhandlers.MessageAddedHandler;
import sk.hasto.semchat.application.eventhandlers.MessageRejectedHandler;
import sk.hasto.semchat.application.eventhandlers.SemanticMessageAddedHandler;
import sk.hasto.semchat.application.impl.ChatServiceImpl;
import sk.hasto.semchat.domain.DomainEventBus;
import sk.hasto.semchat.domain.events.MessageAdded;
import sk.hasto.semchat.domain.events.MessageRejected;
import sk.hasto.semchat.domain.events.NonSemanticMessageAdded;
import sk.hasto.semchat.domain.events.SemanticMessageAdded;
import sk.hasto.semchat.domain.services.Annotator;
import sk.hasto.semchat.domain.services.OntologyGenerator;
import sk.hasto.semchat.domain.services.SimilarityComputer;
import sk.hasto.semchat.domain.services.impl.SimpleSimilarityComputer;
import sk.hasto.semchat.domain.services.repositories.ChatSegmentRepository;
import sk.hasto.semchat.domain.services.repositories.MessageRepository;
import sk.hasto.semchat.infrastructure.persistence.db4o.ChatSegmentRepositoryDb4o;
import sk.hasto.semchat.infrastructure.persistence.db4o.MessageRepositoryDb4o;
import sk.hasto.semchat.infrastructure.services.gate.AnnotatorImpl;
import sk.hasto.semchat.infrastructure.services.gate.SimpleOntologyGenerator;

/**
 * Sluzi na inicializaciu aplikacie.
 * @author Branislav Hasto
 */
public final class SemchatInitializer implements ServletContextListener
{
	private static final Logger logger = Logger.getLogger(SemchatInitializer.class.getName());

	// konfiguracia
	private final int messageCount = 25;
	private final float minSimilarity = 0.5f;

	// instancie tried, ktore potrebujeme na konci cisto uvolnit
	private Ontology ontology;
	private Gazetteer gazetteer;
	private ObjectContainer db;


	public void contextInitialized(ServletContextEvent sce)
	{
		try {
			ServletContext context = sce.getServletContext();
			// TODO: zisti, ci sa to da spravit lepsie, toto nemusi fungovat vsade
			File webInfPath = new File(context.getRealPath("/WEB-INF"));

			// init processing resources
			GateInitializer.init(new File(webInfPath, "gate"));
			GateResourceFactory factory = new GateResourceFactory();
//			URL ontologyUrl = context.getResource("/WEB-INF/data/ontologies/astronomy.xml");
//			URL mappings = context.getResource("/WEB-INF/data/ontologies/mappings.txt");
//			ontology = factory.newOntology(ontologyUrl, mappings);
			ontology = newFakeOntology();
			gazetteer = factory.newOntologyBasedGazetteer(ontology);
			OntologyGenerator generator = new SimpleOntologyGenerator(ontology);
			SimilarityComputer computer = new SimpleSimilarityComputer();
			Annotator annotator = new AnnotatorImpl(gazetteer);

			// init database
			db = Db4oEmbedded.openFile("chat.db4o");
			ChatSegmentRepository segmentRepository = new ChatSegmentRepositoryDb4o(db, computer);
			MessageRepository messageRepository = new MessageRepositoryDb4o(db);

			// init chat service and make IoC via service locator
			ChatService chatService = new ChatServiceImpl(annotator, messageRepository, segmentRepository, minSimilarity, messageCount);
			context.setAttribute("chatService", chatService);

			// register event handlers
			DomainEventBus.subscribe(new MessageAddedHandler<MessageAdded>(segmentRepository), NonSemanticMessageAdded.class);
			DomainEventBus.subscribe(new SemanticMessageAddedHandler(generator, segmentRepository), SemanticMessageAdded.class);
			DomainEventBus.subscribe(new MessageRejectedHandler(), MessageRejected.class);
		}
//		catch (IOException ex) {
//			logger.log(Level.SEVERE, "Error reading application settings.", ex);
//			throw new RuntimeException(ex);
//		}
		catch (GateException ex) {
			logger.log(Level.SEVERE, "GATE could not be initalized.", ex);
			throw new RuntimeException(ex);
		}
	}


	public void contextDestroyed(ServletContextEvent sce)
	{
		// free GATE resources via factory
		if (ontology != null) {
			Factory.deleteResource(ontology);
		}
		
		if (gazetteer != null) {
			Factory.deleteResource(gazetteer);
		}

		// close db connections
		if (db != null) {
			db.close();
		}
	}



	private static Ontology newFakeOntology() throws ResourceInstantiationException
	{
		try {
			Ontology ontology = (Ontology) Factory.createResource(OWLIMOntology.class.getName());
			ontology.setDefaultNameSpace("http://test#");
			ontology.setURL(new URL("http://test#"));

			// classes
			OClass club = ontology.addOClass(ontology.createOURIForName("club"));
			OClass footballClub = ontology.addOClass(ontology.createOURIForName("football_club"));
			OClass hockeyClub = ontology.addOClass(ontology.createOURIForName("hockey_club"));
			OClass person = ontology.addOClass(ontology.createOURIForName("person"));
			OClass sportsman = ontology.addOClass(ontology.createOURIForName("sportsman"));
			OClass footballPlayer = ontology.addOClass(ontology.createOURIForName("football_player"));
			OClass hockeyPlayer = ontology.addOClass(ontology.createOURIForName("hockey_player"));
			OClass tennisPlayer = ontology.addOClass(ontology.createOURIForName("tennis_player"));

			// hierarchy
			club.addSubClass(footballClub);
			club.addSubClass(hockeyClub);
			person.addSubClass(sportsman);
			sportsman.addSubClass(footballPlayer);
			sportsman.addSubClass(hockeyPlayer);
			sportsman.addSubClass(tennisPlayer);

			// instances
			ontology.addOInstance(ontology.createOURIForName("napoli"), footballClub);
			ontology.addOInstance(ontology.createOURIForName("barcelona"), footballClub);
			ontology.addOInstance(ontology.createOURIForName("hamsik"), footballPlayer);
			ontology.addOInstance(ontology.createOURIForName("skrtel"), footballPlayer);
			ontology.addOInstance(ontology.createOURIForName("stoch"), footballPlayer);
			ontology.addOInstance(ontology.createOURIForName("satan"), hockeyPlayer);
			ontology.addOInstance(ontology.createOURIForName("palfy"), hockeyPlayer);
			ontology.addOInstance(ontology.createOURIForName("gaborik"), hockeyPlayer);
			ontology.addOInstance(ontology.createOURIForName("federer"), tennisPlayer);
			ontology.addOInstance(ontology.createOURIForName("nadal"), tennisPlayer);
			ontology.addOInstance(ontology.createOURIForName("hrbaty"), tennisPlayer);

			return ontology;
		}
		catch (MalformedURLException ex) {
			// url creation under control
			throw new AssertionError(ex);
		}
	}


}
