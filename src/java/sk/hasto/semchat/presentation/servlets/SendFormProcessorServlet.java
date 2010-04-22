package sk.hasto.semchat.presentation.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sk.hasto.semchat.application.ChatWriteService;
import sk.hasto.semchat.domain.model.User;


/**
 * Spracovava odoslany formular.
 * @author Branislav Hasto
 */
public final class SendFormProcessorServlet extends HttpServlet
{
	private static final long serialVersionUID = 4994378164794883331L;

	/* chat sluzba */
	private ChatWriteService chatService;


	@Override
	public void init() throws ServletException
	{
		// IoC through Service Locator
		chatService = (ChatWriteService) getServletContext().getAttribute("chatService");

		if (chatService == null) {
			throw new UnavailableException("Chat service is not available.");
		}
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		// posle na prihlasovanie, ak treba
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			response.sendRedirect("login.jsp");
		}

		String text = request.getParameter("text");
		if (text == null) {
			throw new ServletException("Parameter text is missing.");
		}

		if (!text.trim().isEmpty()) {
			chatService.addMessage(text, user);
		}

		response.sendRedirect("");
	}

}
