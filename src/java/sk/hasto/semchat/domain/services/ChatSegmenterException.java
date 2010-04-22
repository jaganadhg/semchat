package sk.hasto.semchat.domain.services;

/**
 * Vynimka pri urcovani segmentov.
 * @author Branislav Hasto
 */
public final class ChatSegmenterException extends Exception
{
	private static final long serialVersionUID = 8692523687942062446L;


	public ChatSegmenterException(Throwable cause)
	{
		super(cause);
	}

}
