package sk.hasto.semchat.domain.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.Validate;

/**
 * Segment chatu. Segment je zoskupenie sprav.
 * @author Branislav Hasto
 */
public final class ChatSegment
{
	/** velkost zasobnika na docasne spravy */
	private static final int MAX_OFFTOPIC_SIZE = 10;

	/** spravy v segmente */
	private final LinkedList<Message> messages = new LinkedList<Message>();

	/** zasobnik docasnych sprav */
	private final LinkedList<Message> offTopicMessages = new LinkedList<Message>();

	/** ontologia reprezentujuca segment */
	private ChatSegmentOntology ontology;

	/** id segmentu */
	private long id;

	/** anotacie - cache */
	private final Set<OntologyAnnotation> annotations = new HashSet<OntologyAnnotation>();


	/**
	 * Pokusi sa pridat spravu do tohto segmentu.
	 * @param message
	 * @return
	 */
	public boolean addMessage(Message message)
	{
		Validate.notNull(message, "Message must not be null.");

		if (accepts(message)) {
			if (message.hasSemanticValue()) {
				messages.addAll(offTopicMessages);
				offTopicMessages.clear();
				messages.add(message);
				annotations.addAll(message.getAnnotations());
			} else {
				offTopicMessages.add(message);
				if (offTopicMessages.size() > MAX_OFFTOPIC_SIZE) {
					offTopicMessages.removeFirst();
				}
				assert offTopicMessages.size() <= MAX_OFFTOPIC_SIZE;
			}

			return true;
		}

		return false;
	}


	/**
	 * Urci, ci sa moze sprava ulozit do tohto segmentu.
	 * @param message
	 * @return
	 */
	public boolean accepts(Message message)
	{
		if (message == null) {
			return false;
		}

		return message.hasSemanticValue() || messages.isEmpty()
			   || offTopicMessages.size() < MAX_OFFTOPIC_SIZE;
	}


	/**
	 * Nastavi ontologiu reprezentujucu tento segment.
	 * Ontologia bude strukturovanou reprezentaciou segmentu.
	 * @param ontology
	 */
	public void setOntology(ChatSegmentOntology ontology)
	{
		this.ontology = ontology;
	}


	/**
	 * @return ontologia reprezentujuca segment
	 */
	public ChatSegmentOntology getOntology()
	{
		return ontology;
	}


	/**
	 * Skontroluje, ci ma segment uz nastavenu ontologiu.
	 * @return
	 */
	public boolean hasOntology()
	{
		return ontology != null;
	}


	/**
	 * @return zjednotenie anotacii vsetkych sprav v segmente
	 */
	public Set<OntologyAnnotation> getAnnotations()
	{
		return Collections.unmodifiableSet(annotations);
	}


	/**
	 * @return zoznam sprav v segmente
	 */
	public List<Message> getMessages()
	{
		return Collections.unmodifiableList(messages);
	}


	/**
	 * Nastavi id segmentu.
	 * @param id
	 */
	public void setId(long id)
	{
		this.id = id;
	}


	/**
	 * @return id segmentu
	 */
	public long getId()
	{
		return id;
	}


	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder(500);
		stringBuilder.append("ID: ");
		stringBuilder.append(id);
		stringBuilder.append('\n');
		stringBuilder.append("Messages: ");
		stringBuilder.append(messages);
		stringBuilder.append('\n');
		stringBuilder.append("Offtopic messages: ");
		stringBuilder.append(offTopicMessages);
		stringBuilder.append('\n');
		stringBuilder.append("Annotations: ");
		stringBuilder.append(annotations);

		if (hasOntology()) {
			stringBuilder.append('\n');
			stringBuilder.append("Ontology: ");
			stringBuilder.append(ontology.toString());
		}

		return stringBuilder.toString();
	}


	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ChatSegment other = (ChatSegment) obj;
		return id == other.id;
	}


	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 17 * hash + (int) (id ^ (id >>> 32));
		return hash;
	}

}
