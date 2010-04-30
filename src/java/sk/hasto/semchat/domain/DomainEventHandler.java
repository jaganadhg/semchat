package sk.hasto.semchat.domain;

/**
 * Domain event handler.
 * @author Branislav Hasto
 */
public interface DomainEventHandler<T extends DomainEvent>
{

	/**
	 * Handles event.
	 * @param event
	 */
	void handle(T event);

}
