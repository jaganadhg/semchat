package sk.hasto.semchat.presentation.dwr;

import java.util.List;
import org.apache.commons.lang.Validate;
import org.directwebremoting.Browser;
import org.directwebremoting.ui.dwr.Util;
import sk.hasto.semchat.application.ChatReadService;
import sk.hasto.semchat.domain.common.Event;
import sk.hasto.semchat.domain.common.Handler;
import sk.hasto.semchat.domain.model.Message;
import sk.hasto.semchat.domain.model.MessageAddedEvent;
import sk.hasto.semchat.presentation.MessageRenderer;

/**
 * Listener pridavania sprav, ktory na baze Comet
 * posiela spravy do vsetkych browserov, kde treba
 * obnovit stranku.
 * @author Branislav Hasto
 */
public final class CometMessageAddedEventHandler implements Handler<MessageAddedEvent>
{
	/* chat sluzba */
	private final ChatReadService chatService;


	public CometMessageAddedEventHandler(ChatReadService chatService)
	{
		Validate.notNull(chatService, "Chat service must not be null.");
		this.chatService = chatService;
	}


	public boolean canHandle(Class<? extends Event> eventType)
	{
		return MessageAddedEvent.class.equals(eventType);
	}


	public void handle(MessageAddedEvent event)
	{
		List<Message> messages = chatService.getLastMessages();

		// pripravi vystupne data
		final String[] items = new String[messages.size()];
		int index = 0;
		for(Message message : messages) {
			items[index++] = MessageRenderer.render(message);
		}

		// vykona upravy vo vsetkych pripojenych browseroch
		Browser.withPage("/semchat/", new Runnable() {
			public void run() {
				Util.removeAllOptions("messages");
				Util.addOptions("messages", items);
			}
		});
	}

}
