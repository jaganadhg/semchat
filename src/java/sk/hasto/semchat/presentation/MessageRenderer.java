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
	 * Vyrenderuje spravu do retazca.
	 * @param message
	 * @return
	 */
	public static String render(Message message)
	{
		Validate.notNull(message, "Message must not be null.");

		String text = message.getText();
		String userName = message.getUser().getName();
		String time = shortTimeFormat.format(message.getTime());

		StringBuilder bldr 
				= new StringBuilder(text.length() + userName.length() + time.length() + 30);
		bldr.append("<span class=\"meta\">");
		bldr.append(userName);
		bldr.append(' ');
		bldr.append(time);
		bldr.append("</span>");
		bldr.append(text);

		return bldr.toString();
	}

}
