package sk.hasto.semchat.infrastructure.persistence.db4o;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.ObjectClass;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;
import sk.hasto.java.collections.MapUtils;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Similarity;
import sk.hasto.semchat.domain.services.SimilarityMeasurer;
import sk.hasto.semchat.domain.services.repositories.ChatSegmentRepository;


/**
 * @author Branislav Hasto
 */
public final class ChatSegmentRepositoryDb4o implements ChatSegmentRepository
{
	private static final Logger logger
			= Logger.getLogger(ChatSegmentRepositoryDb4o.class.getName());

	/** konfiguracia databazy */
	private static final EmbeddedConfiguration dbConfig;

	/** meno suboru s databazou */
	private final static String DB_FILENAME = "segments.db4o";

	/** pocitac podobnosti pre hladanie podla podobnosti */
	private final SimilarityMeasurer measurer;

	/** db4o databaza */
	private final ObjectContainer db;


	static {
		dbConfig = Db4oEmbedded.newConfiguration();
		ObjectClass chatSegmentConfig = dbConfig.common().objectClass(ChatSegment.class);
		chatSegmentConfig.minimumActivationDepth(15);
		chatSegmentConfig.updateDepth(15);
	}


	public ChatSegmentRepositoryDb4o(SimilarityMeasurer measurer)
	{
		Validate.notNull(measurer, "Similarity measurer must not be null.");
		this.measurer = measurer;
		db = Db4oEmbedded.openFile(dbConfig, DB_FILENAME);
	}


	public ChatSegment getLast()
	{
		List<ChatSegment> segments = db.query(ChatSegment.class);
		ChatSegment last = segments.isEmpty() ? null : segments.get(segments.size() - 1);
		if (last != null) {
			last.setId(db.ext().getID(last));
		}
		return last;
	}


	public LinkedHashMap<ChatSegment, Similarity> getSimilarSegments(ChatSegment segment,
																	 Similarity minSimilarity)
	{
		Validate.notNull(minSimilarity, "Minimal similarity must not be null.");

		if (segment == null) {
			return new LinkedHashMap<ChatSegment, Similarity>();
		}

		List<ChatSegment> allSegments = db.query(ChatSegment.class);
		Map<ChatSegment, Similarity> segments
				= new HashMap<ChatSegment, Similarity>(allSegments.size());

		for (ChatSegment dbSegment : allSegments) {
			if (!segment.equals(dbSegment)) {
				Similarity similarity = measurer.measure(segment, dbSegment);
				if (similarity.compareTo(minSimilarity) >= 0) {
					segments.put(dbSegment, similarity);
				}
			}
		}

		return MapUtils.sortMapByValue(segments);
	}


	public void store(ChatSegment segment)
	{
		Validate.notNull(segment, "Segment must not be null.");
		db.store(segment);
	}


	/**
	 * Zavrie spojenie s databazou.
	 */
	public void close()
	{
		db.close();
	}

}
