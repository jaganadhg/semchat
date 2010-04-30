package sk.hasto.semchat.domain;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;

/**
 * Domain event bus.
 * @author Branislav Hasto
 */
public final class DomainEventBus
{

	/** maps event type to handlers for the type */
	private static final Map<Class<?>, Queue<DomainEventHandler<?>>> handlers
			= new HashMap<Class<?>, Queue<DomainEventHandler<?>>>();


	/**
	 * Registers event handler.
	 * @param handler
	 */
	public synchronized static <T extends DomainEvent> void
			subscribe(DomainEventHandler<T> handler, Class<? extends T> eventType)
	{
		Validate.notNull(handler, "Handler is null.");
		Validate.notNull(eventType, "Event type is null.");

		Queue<DomainEventHandler<?>> handlersForType = handlers.get(eventType);
		if (handlersForType == null) {
			handlersForType = new LinkedList<DomainEventHandler<?>>();
			handlers.put(eventType, handlersForType);
		}
		handlersForType.add(handler);
	}


	/**
	 * Publishes event to bus.
	 * Subscribed handlers for this event type will be called.
	 * @param <T>
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public synchronized static <T extends DomainEvent> void publish(T event)
	{
		Validate.notNull(event, "Event is null.");

		Queue<DomainEventHandler<?>> handlersForType = handlers.get(event.getClass());
		if (handlersForType != null) {
			for (DomainEventHandler<?> handler : handlersForType) {
				Logger.getAnonymousLogger().info("Handler: " + handler);
				((DomainEventHandler<T>) handler).handle(event);
			}
		}
	}

}
