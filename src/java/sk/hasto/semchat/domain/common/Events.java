package sk.hasto.semchat.domain.common;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.Validate;

/**
 * Events container.
 * @author Branislav Hasto
 */
public final class Events
{
	/** handlery eventov */
	private static final List<Handler<?>> handlers = new LinkedList<Handler<?>>();


	/**
	 * Registers event handler.
	 * @param handler
	 */
	public static void register(Handler<?> handler)
	{
		Validate.notNull(handler, "Handler must not be null.");
		handlers.add(handler);
	}


	/**
	 * Raises event. Calls every handler for the type of event.
	 * @param <T>
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Event> void raise(T event)
	{
		Validate.notNull(event, "Event must not be null.");

		for (Handler<?> handler : handlers) {
			if (handler.canHandle(event.getClass())) {
				((Handler<T>) handler).handle(event);
			}
		}
	}

}
