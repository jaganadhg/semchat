package sk.hasto.semchat.presentation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.commons.lang.Validate;
import sk.hasto.semchat.domain.model.Message;

/**
 * Pomocna trieda na renderovanie sprav.
 * Dovod je, aby spravy boli jednotne zobrazene vsade.
 * @author Branislav Hasto
 */
public final class MessageRenderer
{
	/** formatovac datumu do slovenskeho formatu */
	private static final DateFormat shortTimeFormat = new SimpleDateFormat("H.mm");;


	/**
	 * Vyrenderuje meta data v sprave.
	 * Za meta data sa tu povazuje informacia o autorovi a cas.
	 * @param message
	 * @return
	 */
	public static String renderMetaData(Message message)
	{
		Validate.notNull(message, "Message must not be null.");

		String userName = message.getUser().getName();
		String time = shortTimeFormat.format(message.getTime());
		return userName + " " + time;
	}

}
