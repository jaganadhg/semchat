package sk.hasto.semchat.presentation.servlets;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import sk.hasto.semchat.application.ChatService;
import sk.hasto.semchat.domain.model.ChatSegment;
import sk.hasto.semchat.domain.model.User;

/**
 * Servlet, ktory sa stara o chatovu cast.
 * @author Branislav Hasto
 */
public final class ChatServlet extends BaseServlet
{
	private static final long serialVersionUID = -118179441095214096L;

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


	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		if (!isAuthenticated(request)) {
			response.sendRedirect("login.html");
			return;
		}

		String servletPath = request.getServletPath();
		if ("POST".equals(request.getMethod())) {
			if ("/send.do".equals(servletPath)) {
				sendCommand(request, response);
			} else {
				throw new ServletException("Unknown command.");
			}
		}

		else if ("GET".equals(request.getMethod())) {
			if ("/index.html".equals(servletPath)) {
				chatPage(request, response);
			} else if ("/segment.html".equals(servletPath)) {
				segmentPage(request, response);
			} else if ("/messages.html".equals(servletPath)) {
				messagesPage(request, response);
			} else if ("/segments-info.html".equals(servletPath)) {
				segmentsInfoPage(request, response);
			} else if ("/logout.do".equals(servletPath)) {
				logoutCommand(request, response);
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
	 * Spracuje prikaz: odoslanie spravy.
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void sendCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		String text = request.getParameter("text");
		if (text == null) {
			throw new ServletException("Message text is missing.");
		}

		text = text.trim();
		User user = getUser(request);
		if (user != null && !text.isEmpty() && text.length() <= 150) {
			chatService.addMessage(text, user);
		}

		response.sendRedirect("index.html");
	}


	/**
	 * Spracuje prikaz: odhlasenie.
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void logoutCommand(HttpServletRequest request, HttpServletResponse response)
			throws IOException
	{
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		response.sendRedirect("login.html");
	}


	/* -----------------------
	 *         PAGES
	 * ----------------------- */

	/**
	 * Pripravi a zobrazi stranku chat.
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void chatPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		request.setAttribute("messages", chatService.getLastMessages());
		request.setAttribute("currentSegment", chatService.getCurrentSegment());
		request.setAttribute("similarSegments", chatService.getSegmentsSimilarToCurrent());

		forwardTo("/WEB-INF/jsp/chat.jsp", request, response);
	}


	/**
	 * Pripravi a zobrazi info o ziadanom segmente chatu.
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void segmentPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		ChatSegment segment;
		try {
			String segmentIdParameter = request.getParameter("id");
			UUID segmentId = UUID.fromString(segmentIdParameter);
			segment = chatService.getSegment(segmentId);
		} catch (IllegalArgumentException ex) {
			segment = null;
		}

		if (segment == null) {
			throw new ServletException("Invalid segment ID.");
		}

		request.setAttribute("segment", segment);
		request.setAttribute("messages", segment.getMessages());

		forwardTo("/WEB-INF/jsp/segment.jsp", request, response);
	}


	/* -----------------------
	 *          AJAX
	 * ----------------------- */

	/**
	 * Pripravi a zobrazi zoznam sprav.
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	private void messagesPage(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		request.setAttribute("messages", chatService.getLastMessages());
		forwardTo("/WEB-INF/jsp/messages.jsp", request, response);
	}


	/**
	 * Pripravi a zobrazi info o stave segmentoch.
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	private void segmentsInfoPage(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		request.setAttribute("currentSegment", chatService.getCurrentSegment());
		request.setAttribute("similarSegments", chatService.getSegmentsSimilarToCurrent());

		forwardTo("/WEB-INF/jsp/segmentsInfo.jsp", request, response);
	}

}
