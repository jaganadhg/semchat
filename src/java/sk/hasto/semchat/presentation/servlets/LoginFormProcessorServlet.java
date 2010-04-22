package sk.hasto.semchat.presentation.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sk.hasto.semchat.domain.model.User;

/**
 * Servlet na spracovanie prihlasovacieho formulara.
 * @author Branislav Hasto
 */
public final class LoginFormProcessorServlet extends HttpServlet
{
	private static final long serialVersionUID = 426042737588015716L;

	
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
       String name = request.getParameter("name");
	   if (name == null) {
		   throw new ServletException("Parameter name is missing.");
	   }

	   if (name.trim().isEmpty()) {
		   response.sendRedirect("login.jsp");
	   }

	   User user = new User(name);
	   request.getSession().setAttribute("user", user);

	   response.sendRedirect("");
    }

}
