package sk.hasto.semchat.presentation.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sk.hasto.semchat.domain.model.User;

/**
 * Zakladna implementacia vsetkych servletov.
 * @author Branislav Hasto
 */
public abstract class BaseServlet extends HttpServlet
{
	private static final long serialVersionUID = 2714154953289631596L;


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
	 * Forwards the request from this servlet to resource on the given path.
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
		request.getRequestDispatcher(path).forward(request, response);
	}


	/**
	 * Includes the response from the given resource within this servlet response.
	 * @param path included resource path
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	protected final void include(String path, HttpServletRequest request,
								 HttpServletResponse response)
								 throws IOException, ServletException
	{
		request.getRequestDispatcher(path).include(request, response);
	}

}
