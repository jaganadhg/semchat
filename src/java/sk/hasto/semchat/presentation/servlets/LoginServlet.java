package sk.hasto.semchat.presentation.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sk.hasto.semchat.domain.model.User;

/**
 * Servlet na spracovanie prihlasovacieho formulara.
 * @author Branislav Hasto
 */
public final class LoginServlet extends BaseServlet
{
	private static final long serialVersionUID = 426042737588015716L;


	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		if (isAuthenticated(request)) {
			response.sendRedirect("index.html");
			return;
		}
		
		String name = request.getParameter("name");
		if (name == null) {
			throw new ServletException("Login is missing.");
		}

		name = name.trim();
		if (name.length() > 15) {
			throw new ServletException("Login is too long.");
		}

		if (name.isEmpty()) {
			response.sendRedirect("login.html");
			return;
		}

		User user = new User(name);
		request.getSession().setAttribute("user", user);

		response.sendRedirect("index.html");
	}


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		if (isAuthenticated(request)) {
			response.sendRedirect("index.html");
			return;
		}

		forwardTo("/WEB-INF/jsps/login.jsp", request, response);
	}

}
