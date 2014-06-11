package rest;

import io.vertigo.kernel.exception.VRuntimeException;

import org.w3c.dom.events.UIEvent;

/**
 * Context de traitement des requ�tes HTTP.
 * 
 * @author npiedeloup
 * @version $Id: RequestContext.java,v 1.12 2010/11/16 10:36:49 pchretien Exp $
 */
public interface RequestContext {

	/**
	 * @return ControllerDefinition de la request
	 */
	ControllerDefinition getControllerDefinition();

	/**
	 * Fixe la ControllerDefinition courante.
	 * @param controllerDefinition nouvelle controllerDefinition
	 */
	void setControllerDefinition(ControllerDefinition controllerDefinition);

	/**
	 * @return Controller courant
	 */
	Context getContext();

	/**
	 * Fixe le controller courant.
	 * @param controller nouveau controller
	 */
	void setContext(Context context);

	/**
	 * @return Controller courant
	 */
	UIController getUIController();

	/**
	 * Fixe le controller courant.
	 * @param controller nouveau controller
	 */
	void setUIController(UIController controller);

	/**
	 * @return Event courant
	 */
	UIEvent getEvent();

	/**
	 * Fixe l'event courant.
	 * @param event nouvel event
	 */
	void setEvent(UIEvent event);

	/**
	 * @return Si la requete � g�n�r�e une exception (et ne doit pas �tre commit�e)
	 */
	boolean hasUserException();

	/**
	 * Enregistre l'exception utilisateur lev�e pendant le traitement.
	 * (ne peut etre fait qu'une seule fois)
	 * @param kue Exception utilisateur
	 */
	void setUserException(VRuntimeException kue);

	/**
	 * R�cup�re l'exception d�clar�e, s'il y en a une.
	 * (NULLABLE)
	 * @return Exception d�clar�e dans cette requete, ou null si pas d'erreur
	 */
	VRuntimeException getException();

	//
	//	/**
	//	 * @return uiContext courant
	//	 */
	//	UIContext getUIContext();
	//
	//	/**
	//	 * Fixe le uiContext courant.
	//	 * @param uiContext nouveau uiContext
	//	 */
	//	void setUIContext(UIContext uiContext);

	/**
	 * @return Structure des erreurs relatives � la page
	 */
	ErrorProcess getErrorProcess();

	/**
	 * @return requestContext qui sera utiliser dans cette branche de Forward
	 */
	RequestContext createForwardContext();
}
