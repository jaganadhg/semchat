package sk.hasto.semchat.presentation.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sk.hasto.semchat.domain.model.User;

/**
 * Servlet, ktory sa stara o prihlasovaciu cast.
 * @author Branislav Hasto
 */
public final class LoginServlet extends BaseServlet
{
	private static final long serialVersionUID = 426042737588015716L;


	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		if (isAuthenticated(request)) {
			response.sendRedirect("index.html");
			return;
		}

		String servletPath = request.getServletPath();
		if ("POST".equals(request.getMethod())) {
			if ("/login.do".equals(servletPath)) {
				loginCommand(request, response);
			} else {
				throw new ServletException("Unknown command.");
			}
		}

		else if ("GET".equals(request.getMethod())) {
			if ("/login.html".equals(servletPath)) {
				loginPage(request, response);
			} else {
				throw new ServletException("Unknown page.");
			}
		}

		else {
			throw new ServletException("Unsupported HTTP method.");
		}
	}


	/* -----------------------
	 *       COMMANDS
	 * ----------------------- */

	/**
	 * Spracuje prikaz: login.
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void loginCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		String name = request.getParameter("name");
		if (name == null) {
			throw new ServletException("Login is missing.");
		}

		name = name.trim();
		if (name.isEmpty() || name.length() > 15) {
			response.sendRedirect("login.html");
			return;
		}

		User user = new User(name);
		request.getSession().setAttribute("user", user);

		response.sendRedirect("index.html");
	}


	/* -----------------------
	 *         PAGES
	 * ----------------------- */

	/**
	 * Pripravi a zobrazi login stranku.
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	private void loginPage(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		forwardTo("/WEB-INF/jsp/login.jsp", request, response);
	}

}
