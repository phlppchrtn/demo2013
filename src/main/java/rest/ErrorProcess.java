package rest;

import io.vertigo.kernel.lang.MessageText;

import java.util.Collection;

/**
 * Gestion des erreurs d'un processus métier.
 *
 * @author plepaisant, pchretien, jcassignol
 * @version $Id: ErrorProcess.java,v 1.13 2011/01/24 16:34:46 npiedeloup Exp $
 */
public interface ErrorProcess /*extends KError*/{
	/**
	 * Purge toutes les erreurs.
	 */
	void clearErrors();

	/**
	 * Enregistre une erreur.
	 *
	 * @param message Message d'erreur
	 */
	void registerErrorSimple(final MessageText message);

	/**
	 * Retourne la liste des dto en erreur.
	 *
	 * @return Noms des champs
	 */
	Collection<MessageText> getErrorSimpleMessage();

	//
	//	/**
	//	 * Peuple la liste de messages d'erreur avec celles de l'input.
	//	 *
	//	 * @param dtoInput Input de DTO qui possède des erreurs ou non
	//	 * @param errorDtObject KErrorDtObject Structure des erreurs du DTO
	//	 */
	//	void registerErrorDtObject(final DtObjectInput<?> dtoInput, ErrorDtObject errorDtObject);
	//
	//	/**
	//	 * Indique s'il y a des erreurs sur le DtInput.	
	//	 * @param dtInput Input qui possède des erreurs ou non
	//	 * @return Si l'input possède des erreurs
	//	 */
	//	boolean hasError(final DtInput<?> dtInput);
	//
	//	/**
	//	* @param dtoInput Input de DTO dans le contexte
	//	* @return Structure des messages d'erreur du DTO
	//	*/
	//	ErrorDtObject getErrorDtObject(final DtObjectInput<?> dtoInput);
	//
	//	/**
	//	 * @param dtcId Clé de l'input de DTC 
	//	 * @return Pile des erreurs du DTC
	//	 */
	//	ErrorDtCollection obtainErrorDtCollection(final String dtcId);
	//
	//	/**
	//	 *
	//	 * @return Set des DTO possédant une pile d'erreurs.
	//	 */
	//	Set<DtObjectInput<?>> getDtObjectErrorSet();
	//
	//	/**
	//	 *
	//	 * @return Set des DTC possédant une pile d'erreurs.
	//	 */
	//	Set<String> getDtCollectionErrorSet();
}
