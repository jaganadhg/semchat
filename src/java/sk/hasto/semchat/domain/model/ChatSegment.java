package sk.hasto.semchat.domain.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;
import sk.hasto.semchat.domain.DomainEventBus;
import sk.hasto.semchat.domain.events.MessageRejected;
import sk.hasto.semchat.domain.events.NonSemanticMessageAdded;
import sk.hasto.semchat.domain.events.OntologyUpdated;
import sk.hasto.semchat.domain.events.SemanticMessageAdded;

/**
 * Segment chatu. Segment je zoskupenie sprav.
 * @author Branislav Hasto
 */
public final class ChatSegment
{
	/** velkost zasobnika na docasne spravy */
	private static final int MAX_OFFTOPIC_COUNT = 10;

	/** id segmentu */
	private final UUID id = UUID.randomUUID();

	/** spravy v segmente */
	private final LinkedList<Message> messages = new LinkedList<Message>();

	/** zasobnik docasnych sprav */
	private final LinkedList<Message> offTopicMessages = new LinkedList<Message>();

	/** ontologia reprezentujuca segment */
	private final ChatSegmentOntology ontology = new ChatSegmentOntology();

	/** vsetky anotacie segmentu */
	private final Set<OntologyAnnotation> annotations = new HashSet<OntologyAnnotation>();


	/**
	 * @return id segmentu
	 */
	public UUID getId()
	{
		return id;
	}


	/**
	 * @return zoznam sprav v segmente
	 */
	public List<Message> getMessages()
	{
		return Collections.unmodifiableList(messages);
	}


	/**
	 * Pokusi sa pridat spravu do tohto segmentu.
	 * @param message
	 * @return
	 */
	public void addMessage(Message message)
	{
		Validate.notNull(message, "Message is null.");

		Logger.getAnonymousLogger().info("Segment received message " + message);
		Logger.getAnonymousLogger().info("Annotations " + message.getAnnotations());
		if (!accepts(message)) {
			DomainEventBus.publish(new MessageRejected(this, message));
		} else if (message.hasSemanticValue()) {
			addSemanticMessage(message);
		}  else {
			addNonSemanticMessage(message);
		}
	}


	/**
	 * Prida spravu bez semantickeho vyznamu.
	 * @param message
	 */
	private void addNonSemanticMessage(Message message)
	{
		offTopicMessages.add(message);
		if (offTopicMessages.size() > MAX_OFFTOPIC_COUNT) {
			offTopicMessages.removeFirst();
		}
		assert offTopicMessages.size() <= MAX_OFFTOPIC_COUNT;
		DomainEventBus.publish(new NonSemanticMessageAdded(this, message));
	}


	/**
	 * Prida spravu so semantickym vyznamom.
	 * @param message
	 */
	private void addSemanticMessage(Message message)
	{
		messages.addAll(offTopicMessages);
		offTopicMessages.clear();
		messages.add(message);
		annotations.addAll(message.getAnnotations());
		DomainEventBus.publish(new SemanticMessageAdded(this, message));
	}


	/**
	 * Urci, ci sa moze sprava ulozit do tohto segmentu.
	 * @param message
	 * @return
	 */
	private boolean accepts(Message message)
	{
		return message != null && (message.hasSemanticValue() || acceptsOffTopicMessage());
	}


	/**
	 * Urci, ci tento segment este moze ulozit spravu bez semantickeho vyznamu.
	 * @param message
	 * @return
	 */
	private boolean acceptsOffTopicMessage()
	{
		return offTopicMessages.size() < MAX_OFFTOPIC_COUNT || messages.isEmpty();
	}


	/**
	 * @return zjednotenie anotacii vsetkych sprav v segmente
	 */
	public Set<OntologyAnnotation> getAnnotations()
	{
		return Collections.unmodifiableSet(annotations);
	}


	/**
	 * @return ontologia reprezentujuca segment
	 */
	public ChatSegmentOntology getOntology()
	{
		return ontology;
	}


	/**
	 * Nastavi ontologiu reprezentujucu tento segment.
	 * Ontologia bude strukturovanou reprezentaciou segmentu.
	 * @param ontology
	 */
	public void updateOntology(ChatSegmentOntology ontology)
	{
		if (!this.ontology.equals(ontology)) {
			this.ontology.updateWith(ontology);
			DomainEventBus.publish(new OntologyUpdated(this));
		}
	}


	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder(500);
		stringBuilder.append("ID: ");
		stringBuilder.append(id);
		stringBuilder.append("\nMessages: ");
		stringBuilder.append(messages);
		stringBuilder.append("\nOfftopic messages: ");
		stringBuilder.append(offTopicMessages);
		stringBuilder.append("\nAnnotations: ");
		stringBuilder.append(annotations);
		stringBuilder.append("\nOntology: ");
		stringBuilder.append(ontology);

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
		if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}


	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
		return hash;
	}

}
