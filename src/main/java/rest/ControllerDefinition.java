package rest;

import io.vertigo.kernel.metamodel.Definition;

/**
 * D�finit les propri�t�s d'un contr�leur.
 *
 * @author  pchretien
 * @version $Id: ControllerDefinition.java,v 1.7 2010/11/16 10:36:47 pchretien Exp $
 */
// EVOL public interface ControllerDefinition extends Definition<ControllerDefinition> {
public interface ControllerDefinition extends Definition {
	/**
	 * @return Nom utilis� dans les URL
	 */
	String getFriendlyName();

	/**
	 * @return URI de la Jsp associ�e au controller
	 */
	String getJspUri();

	/**
	 * @return Si besoin d'une authentification pour la page.
	 */
	boolean needsAuthentification();

	/**
	 * @return Si besoin d'une session  pour la page.
	 */
	boolean needsSession();

	/**
	 * Retourne la classe d'impl�mentation du controller.
	 *
	 * @return Classe du controller
	 */
	String getControllerClassName();
	//
	//	/**
	//	 * Retourne le champ correspondant SOUS CONDITION qu'il existe sinon assertion.
	//	 *
	//	 * @param actionName Nom de l'action
	//	 * @return Type d'action correspondante
	//	 */
	//	ActionDefinition getActionDefinition(String actionName);
	//
	//	/**
	//	 * @return Collection des types d'action.
	//	 */
	//	Collection<ActionDefinition> getActionDefinitionCollection();
}
