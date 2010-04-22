package sk.hasto.semchat.presentation.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sk.hasto.semchat.domain.model.ChatSegment;

/**
 * Stara sa o zobrazovanie konkretnych segmentov.
 * @author Branislav Hasto
 */
public final class SegmentServlet extends BaseServlet
{
	private static final long serialVersionUID = 6796038817108223545L;


	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		if (!isAuthenticated(request)) {
			response.sendRedirect("login.html");
			return;
		}

		ChatSegment segment;
		try {
			String segmentIdParameter = request.getParameter("id");
			long segmentId = Long.parseLong(segmentIdParameter);
			segment = chatService.getSegment(segmentId);
		} catch (NumberFormatException ex) {
			segment = null;
		}

		if (segment == null) {
			throw new ServletException("Invalid segment ID.");
		}

		request.setAttribute("segment", segment);
		forwardTo("/WEB-INF/jsps/segment.jsp", request, response);
    } 

}
