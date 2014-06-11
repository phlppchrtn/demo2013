package rest;

import org.w3c.dom.events.UIEvent;

/**
 * Controller de l'interface utilisateur.
 * Permet d'évaluer des évenements utilisateurs cf. UIEvent.
 * @author npiedeloup
 * @version $Id: UIController.java,v 1.8 2010/11/16 10:36:49 pchretien Exp $
 */
public interface UIController {

	/**
	 * Gestion d'un evenement.
	 * @param event Event de l'interface utilisateur à traiter.
	 * @return Message indiquant la suite à donner.
	 * @throws KUserException Exception utilisateur
	 * @throws KSystemException Exception système
	 */
	Message onEvent(UIEvent event);

}
