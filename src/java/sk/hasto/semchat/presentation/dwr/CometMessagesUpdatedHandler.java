package sk.hasto.semchat.presentation.dwr;

import org.directwebremoting.ScriptSessions;
import sk.hasto.semchat.domain.DomainEventHandler;
import sk.hasto.semchat.domain.events.MessagesUpdated;

/**
 * @author Branislav Hasto
 */
public class CometMessagesUpdatedHandler implements DomainEventHandler<MessagesUpdated>
{

	public void handle(MessagesUpdated event)
	{
		ScriptSessions.addFunctionCall("refreshMessages");
	}

}
