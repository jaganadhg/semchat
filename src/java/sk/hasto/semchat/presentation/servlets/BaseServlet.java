package sk.hasto.semchat.presentation.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sk.hasto.semchat.application.ChatService;
import sk.hasto.semchat.domain.model.User;

/**
 * Zakladna implementacia vsetkych servletov.
 * @author Branislav Hasto
 */
public abstract class BaseServlet extends HttpServlet
{
	private static final long serialVersionUID = 2714154953289631596L;

	/* chat sluzba */
	protected ChatService chatService;


	@Override
	public void init() throws ServletException
	{
		// IoC through Service Locator
		chatService = (ChatService) getServletContext().getAttribute("chatService");

		if (chatService == null) {
			throw new UnavailableException("Chat service is not available.");
		}
	}


	/**
	 * Overi, ci je pouzivatel prihlaseny.
	 * @return
	 */
	protected final boolean isAuthenticated(HttpServletRequest request)
	{
		return getUser(request) != null;
	}


	/**
	 * Ziska pouzivatela z requestu.
	 * @param request
	 * @return
	 */
	protected final User getUser(HttpServletRequest request)
	{
		User user = (User) request.getSession().getAttribute("user");
		return user;
	}


	/**
	 * Forwards a request from a servlet to resource on the given path.
	 * Path must begin with / and is interpreted as relative to the current context root.
	 * @param path destination resource path
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	protected final void forwardTo(String path, HttpServletRequest request, 
								   HttpServletResponse response)
								   throws IOException, ServletException
	{
		getServletContext().getRequestDispatcher(path).forward(request, response);
	}

}
