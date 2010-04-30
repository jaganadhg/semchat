package sk.hasto.semchat.presentation.dwr;

import org.directwebremoting.ScriptSessions;
import sk.hasto.semchat.domain.DomainEventHandler;
import sk.hasto.semchat.domain.events.SegmentsUpdated;

/**
 * @author Branislav Hasto
 */
public class CometSegmentsUpdatedHandler implements DomainEventHandler<SegmentsUpdated>
{

	public void handle(SegmentsUpdated event)
	{
		ScriptSessions.addFunctionCall("refreshSegments");
	}

}
