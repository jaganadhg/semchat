package sk.hasto.semchat.infrastructure.persistence.db4o;

import com.db4o.ObjectContainer;
import com.db4o.config.ObjectClass;
import com.db4o.query.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang.Validate;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Similarity;
import sk.hasto.semchat.domain.services.SimilarityComputer;
import sk.hasto.semchat.domain.services.repositories.ChatSegmentRepository;


/**
 * @author Branislav Hasto
 */
public final class ChatSegmentRepositoryDb4o extends AbstractRepositoryDb4o<ChatSegment>
											 implements ChatSegmentRepository
{

	/** pocitac podobnosti pre hladanie podla podobnosti */
	private final SimilarityComputer computer;

	
	public ChatSegmentRepositoryDb4o(ObjectContainer db, SimilarityComputer computer)
	{
		super(db);
		Validate.notNull(computer, "Similarity computer is null.");
		this.computer = computer;
		this.db.ext().configure().activationDepth(15);
		this.db.ext().configure().updateDepth(15);
	}


	public ChatSegment getLast()
	{
		List<ChatSegment> segments = db.query(ChatSegment.class);
		ChatSegment last = segments.isEmpty() ? null : segments.get(segments.size() - 1);
		return last;
	}


	public ChatSegment getById(final UUID id)
	{
		Predicate<ChatSegment> idPredicate = new Predicate<ChatSegment>() {
			private static final long serialVersionUID = 3827951647137793012L;
			@Override
			public boolean match(ChatSegment candidate) {
				return candidate.getId().equals(id);
			}
		};
		List<ChatSegment> found = db.query(idPredicate);
		
		return found.isEmpty() ? null : found.get(0);
	}

	
	public List<Similarity> findSimilarSegments(ChatSegment segment,
												float minSimilarity)
	{
		if (segment == null) {
			return Collections.emptyList();
		}

		List<ChatSegment> dbSegments = db.query(ChatSegment.class);

		List<Similarity> results = new ArrayList<Similarity>(dbSegments.size());
		for (ChatSegment dbSegment : dbSegments) {
			if (!segment.equals(dbSegment)) {
				Similarity similarity = computer.compute(segment, dbSegment);
				if (similarity.getValue() >= minSimilarity) {
					results.add(similarity);
				}
			}
		}

		Collections.sort(results, Collections.reverseOrder());
		return results;
	}

}
