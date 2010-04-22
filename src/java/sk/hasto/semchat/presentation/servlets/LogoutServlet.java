package sk.hasto.semchat.presentation.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Spracuje pouzivatelsky prikaz na odhlasenie.
 * @author Branislav Hasto
 */
public final class LogoutServlet extends HttpServlet
{
	private static final long serialVersionUID = -5359356006825773573L;
   

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
	{
		HttpSession session = request.getSession(false);
        if (session != null) {
			session.invalidate();
		}

		response.sendRedirect("login.jsp");
    } 

}
