package rest;

import org.w3c.dom.events.UIEvent;

/**
 * Controller de l'interface utilisateur.
 * Permet d'�valuer des �venements utilisateurs cf. UIEvent.
 * @author npiedeloup
 * @version $Id: UIController.java,v 1.8 2010/11/16 10:36:49 pchretien Exp $
 */
public interface UIController {

	/**
	 * Gestion d'un evenement.
	 * @param event Event de l'interface utilisateur � traiter.
	 * @return Message indiquant la suite � donner.
	 * @throws KUserException Exception utilisateur
	 * @throws KSystemException Exception syst�me
	 */
	Message onEvent(UIEvent event);

}
