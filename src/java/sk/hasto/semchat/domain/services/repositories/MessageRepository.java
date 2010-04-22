/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.hasto.semchat.domain.services.repositories;

import java.util.List;
import sk.hasto.semchat.domain.common.Repository;
import sk.hasto.semchat.domain.model.Message;

/**
 * Ulozisko sprav.
 * @author Branislav Hasto
 */
public interface MessageRepository extends Repository<Message>
{

	/**
	 * Ulozi spravu.
	 * @param message
	 */
	void store(Message message);


	/**
	 * Vrati posledne (najnovsie) spravy v zadanom pocte.
	 * @param count
	 * @return
	 */
	List<Message> getLast(int count);

}
