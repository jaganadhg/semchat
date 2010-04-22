package sk.hasto.semchat.domain.common;

/**
 * Generic event handler.
 * @author Branislav Hasto
 */
public interface Handler<T extends Event>
{

	/**
	 * Indicates whether this event listener can handle events of the given type.
	 * Should return <code>true</code> if <code>eventType</code> is type <code>Class<T></code>.
	 * @param eventType
	 * @return
	 */
	boolean canHandle(Class<? extends Event> eventType);


	/**
	 * Handles domain event.
	 * @param event
	 */
	void handle(T event);

}
