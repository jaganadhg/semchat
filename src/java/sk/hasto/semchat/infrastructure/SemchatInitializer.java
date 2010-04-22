/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.hasto.semchat.infrastructure;

import sk.hasto.semchat.infrastructure.services.gate.GateInitializer;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import gate.creole.gazetteer.Gazetteer;
import gate.creole.ontology.OClass;
import gate.creole.ontology.Ontology;
import gate.creole.ontology.impl.sesame.OWLIMOntology;
import gate.util.GateException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import sk.hasto.semchat.presentation.dwr.CometMessageAddedEventHandler;
import sk.hasto.semchat.application.impl.ChatServiceImpl;
import sk.hasto.semchat.domain.common.Events;
import sk.hasto.semchat.domain.common.Handler;
import sk.hasto.semchat.domain.model.MessageAddedEvent;
import sk.hasto.semchat.domain.services.Annotator;
import sk.hasto.semchat.domain.services.ChatSegmenter;
import sk.hasto.semchat.domain.services.OntologyGenerator;
import sk.hasto.semchat.domain.services.SimilarityMeasurer;
import sk.hasto.semchat.domain.services.impl.ChatSegmenterImpl;
import sk.hasto.semchat.domain.services.impl.SimpleSimilarityMeasurer;
import sk.hasto.semchat.infrastructure.persistence.db4o.ChatSegmentRepositoryDb4o;
import sk.hasto.semchat.infrastructure.persistence.db4o.MessageRepositoryDb4o;
import sk.hasto.semchat.infrastructure.services.gate.AnnotatorImpl;
import sk.hasto.semchat.infrastructure.services.gate.GateResourceFactory;
import sk.hasto.semchat.infrastructure.services.gate.SimpleOntologyGenerator;

/**
 * Sluzi na inicializaciu aplikacie.
 * @author Branislav Hasto
 */
public final class SemchatInitializer implements ServletContextListener
{
	private static final Logger logger = Logger.getLogger(SemchatInitializer.class.getName());

	// Instancie tried, ktore potrebujeme na konci cisto uvolnit.
	private Ontology ontology;
	private Gazetteer gazetteer;
	private ChatSegmentRepositoryDb4o segmentRepository;
	private MessageRepositoryDb4o messageRepository;


	public void contextInitialized(ServletContextEvent sce)
	{
		try {
			// inject dependencies
			new GateInitializer().init();
			ontology = newFakeOntology();
			gazetteer = new GateResourceFactory().newOntologyBasedGazetteer(ontology);
			Annotator annotator = new AnnotatorImpl(gazetteer);
			ChatSegmenter chatSegmenter = new ChatSegmenterImpl(annotator);
			OntologyGenerator ontologyGenerator = new SimpleOntologyGenerator(ontology);
			SimilarityMeasurer measurer = new SimpleSimilarityMeasurer();
			segmentRepository = new ChatSegmentRepositoryDb4o(measurer);
			messageRepository = new MessageRepositoryDb4o();
			ChatServiceImpl chatService = new ChatServiceImpl(chatSegmenter, ontologyGenerator,
					segmentRepository, messageRepository);

			
			// IoC via service locator (only simple way to inject dependencies into servlets)
			sce.getServletContext().setAttribute("chatService", chatService);

			// event handlers
			Handler<MessageAddedEvent> handler = new CometMessageAddedEventHandler(chatService);
			Events.register(handler);
		}

		catch (GateException ex) {
			logger.log(Level.SEVERE, "GATE could not be initalized.", ex);
			throw new RuntimeException(ex);
		}
	}

	
	public void contextDestroyed(ServletContextEvent sce)
	{
		// GATE resources treba uvolnit cez factory.
		if (ontology != null) {
			Factory.deleteResource(ontology);
		}
		
		if (gazetteer != null) {
			Factory.deleteResource(gazetteer);
		}

		// Uzavretie databazovych spojeni.
		if (segmentRepository != null) {
			segmentRepository.close();
		}
		
		if (messageRepository != null) {
			messageRepository.close();
		}
	}


	private static Ontology newFakeOntology() throws ResourceInstantiationException
	{
		try {
			Ontology ontology = (Ontology) Factory.createResource(OWLIMOntology.class.getName());
			ontology.setURL(new URL("http://test"));
			ontology.setDefaultNameSpace("http://test#");

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
			// URL creation under control
			throw new AssertionError(ex);
		}
	}

	

}
