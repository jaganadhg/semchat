package sk.hasto.semchat.presentation.dwr;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import sk.hasto.semchat.application.ChatWriteService;
import sk.hasto.semchat.domain.model.User;

/**
 * Javascript proxy for {@link ChatWriteService}.
 * @author Branislav Hasto
 */
public final class ChatServiceDwrProxy
{

	/**
	 * Proxy for method {@link ChatWriteService#addMessage(String, User)}.
	 * @param text message text
	 * @param session added automatically by dwr
	 */
	public void addMessage(String text, ServletContext context, HttpSession session)
	{
		ChatWriteService chatService = (ChatWriteService) context.getAttribute("chatService");
		User user = (User) session.getAttribute("user");

		text = text.trim();
		if (user != null && !text.isEmpty() && text.length() <= 150) {
			chatService.addMessage(text, user);
		}
	}

}
