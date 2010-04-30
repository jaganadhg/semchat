package sk.hasto.semchat.domain.events;

import sk.hasto.semchat.domain.DomainEvent;

/**
 * Udalost: nastala zmena v spravach.
 * Tato udalost je tu iba kvoli prezentacnej vrstve.
 * Nie je to velmi ciste, lebo tento isty ucel zachytavaju aj ine
 * udalosti, ale ja potrebujem udalost az po zapise do databazy.
 * TODO: zvazit, kam tieto veci presunut.
 *
 * @author Branislav Hasto
 */
public class MessagesUpdated implements DomainEvent {}
