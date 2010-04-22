package sk.hasto.semchat.application;

import sk.hasto.semchat.domain.model.User;

/**
 * Chat sluzba umoznujuca zapisovanie udajov.
 * @author Branislav Hasto
 */
public interface ChatWriteService
{

	/**
	 * Vlozi spravu do chatu.
	 * @param text text spravy
	 * @param user autor spravy
	 */
	void addMessage(String text, User user);

}
