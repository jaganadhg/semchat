package sk.hasto.semchat.presentation.servlets;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.Message;
import sk.hasto.semchat.domain.model.Similarity;
import sk.hasto.semchat.domain.model.User;

/**
 * Servlet na obsluhu uvodnej stranky.
 * @author Branislav Hasto
 */
public final class IndexServlet extends BaseServlet
{
	private static final long serialVersionUID = -118179441095214096L;


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		if (!isAuthenticated(request)) {
			response.sendRedirect("login.html");
			return;
		}

		List<Message> messages = chatService.getLastMessages();
		request.setAttribute("messages", messages);

		ChatSegment currentSegment = chatService.getCurrentSegment();
		request.setAttribute("currentSegment", currentSegment);

		List<Similarity> similarSegments
				= chatService.getSegmentsSimilarToCurrent();
		request.setAttribute("similarSegments", similarSegments);

		forwardTo("/WEB-INF/jsps/chat.jsp", request, response);
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		if (!isAuthenticated(request)) {
			response.sendRedirect("login.html");
			return;
		}

		String text = request.getParameter("text");
		if (text == null) {
			throw new ServletException("Message text is missing.");
		}

		text = text.trim();
		if (text.length() > 150) {
			throw new ServletException("Message text is too long.");
		}

		User user = getUser(request);
		if (!text.isEmpty()) {
			chatService.addMessage(text, user);
		}

		response.sendRedirect("index.html");
	}


}
