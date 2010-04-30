package acceptance;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import java.io.File;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Similarity;
import sk.hasto.semchat.domain.services.impl.SimpleSimilarityComputer;
import sk.hasto.semchat.domain.services.repositories.ChatSegmentRepository;
import sk.hasto.semchat.infrastructure.persistence.db4o.ChatSegmentRepositoryDb4o;

/**
 * Testuje, ci sa cela aplikacia sprava korektne.
 * @author Branislav Hasto
 */
public class ApplicationTest
{
	private static ObjectContainer sampleDb;
	private static ObjectContainer corpusDb;
	private static ChatSegmentRepository corpusRepository;


	@BeforeClass
	public static void setUpClass() throws Exception
	{
		File webInfDir = new File("d:/Work/dev/workspace/java/chatter/web/WEB-INF");
		File testDataDir = new File(webInfDir, "data/test");

		/*
		 * Objasnenie pojmov
		 * korpus - vela dokumentov zoskupenych v kategoriach
		 * vzorka - malo dokumentov, ktore sa budeme snazit zaradit do kategorii
		 */
		SegmentsDbGenerator dbGenerator = new SegmentsDbGenerator(webInfDir);

		// ak nemame testovaci korpus v databaze, prejdeme vsetky dokumenty
		// a ulozime ich do databazy (citat to vzdy z disku je nerealne - vykon)
		File corpusDbFile = new File(testDataDir, "corpus.db4o");
		if (!corpusDbFile.exists()) {
			dbGenerator.generate(corpusDbFile, new File(testDataDir, "corpus"));
		}

		// to iste pre vzorku
		File sampleDbFile = new File(testDataDir, "sample.db4o");
		if (!sampleDbFile.exists()) {
			dbGenerator.generate(sampleDbFile, new File(testDataDir, "sample"));
		}

		// databazy
		sampleDb = Db4oEmbedded.openFile(sampleDbFile.getPath());
		sampleDb.ext().configure().activationDepth(15);
		corpusDb = Db4oEmbedded.openFile(corpusDbFile.getPath());
		corpusRepository = new ChatSegmentRepositoryDb4o(corpusDb, new SimpleSimilarityComputer());
	}


	@AfterClass
	public static void tearDownClass()
	{
		if (sampleDb != null) {
			sampleDb.close();
		}

		if (corpusDb != null) {
			corpusDb.close();
		}
	}


	@Test
	public void displaySimilaritySearchResults()
	{
		float minSimilarity = 0.4f;
		List<ChatSegment> segments = sampleDb.query(ChatSegment.class);
		for (ChatSegment segment : segments) {
			List<Similarity> results = corpusRepository.findSimilarSegments(segment, minSimilarity);
			System.out.println("Results for segment " + segment.getId());
			System.out.println("Original Ontology: " + segment.getOntology());
			for (Similarity result : results) {
				System.out.println("Similarity: " + result.getValue());
				System.out.println("Target ontology: " + result.getTarget().getOntology());
				System.out.println("Common classes: " + result.getCommonClasses());
				System.out.println("Common instances: " + result.getCommonInstances());
			}
			System.out.println("\n**************************************************************");
			System.out.println("**************************************************************\n");
		}
	}

}
