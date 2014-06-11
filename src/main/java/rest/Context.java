package rest;

import kasper.domain.model.DtObject;

/**
 * Contexte d'une page.
 * Ce contexte (lourd) est utilis� pour : <ul>
 * <li>Ex�cuter les actions dans les contr�leurs,</li>
 * <li>Afficher les donn�es dans les pages JSP, export CSV...</li>
 * </ul>
 *
 * Le contexte sait se s�rialiser sous la forme d'une chaine de caract�res ;
 * une fois la s�rialisation effectu�e le contexte est non modifiable.
 *
 * Le contexte pr�sente une API tr�s comparable � celle de Map � la diff�rence suivante :<br>
 * La m�thode put n'admet pas de valeur null, corrolaire les get retournent n�cessairement un objet non null.
 *
 * @author  ovannetzel
 * @version $Id: Context.java,v 1.8 2010/11/16 10:36:49 pchretien Exp $
 */
public interface Context {
	/**
	 * Retourne un object enregistr� dans le context du controller.<br>
	 * Cet objet est non null.
	 * @param <J> Type java de l'objet recherch�
	 * @param key Cl� d'enregistrement de l'objet
	 * @return Object enregistr�.
	 */
	<J> J get(final String key);

	/**
	 * Accesseur typ�.
	 * @param key Cl� d'enregistrement de l'objet
	 * @return Objet enregistr�.
	 */
	String getString(final String key);

	/**
	 * Accesseur typ�.
	 * @param <D> Type de DtObject
	 * @param key Cl� d'enregistrement de l'objet
	 * @return Input enregistr�.
	 */
	<D extends DtObject> DtObjectInput<D> getDtObjectInput(final String key);

	/**
	 * Accesseur typ�.
	 * @param <D> Type de DtObject
	 * @param key Cl� d'enregistrement de l'objet
	 * @return Input enregistr�.
	 */
	<D extends DtObject> DtCollectionInput<D> getDtCollectionInput(final String key);

	/**
	 * Accesseur typ�.
	 * @param key Cl� d'enregistrement de l'objet
	 * @return Objet enregistr�.
	 */
	Long getLong(final String key);

	/**
	 * Accesseur typ�.
	 * @param key Cl� d'enregistrement de l'objet
	 * @return Objet enregistr�.
	 */
	Integer getInteger(final String key);

	/**
	 * Accesseur typ�.
	 * @param key Cl� d'enregistrement de l'objet
	 * @return Objet enregistr�.
	 */
	Boolean getBoolean(final String key);

	/**
	 * @param key Cl� d'enregistrement de l'objet
	 * @return Si le controller contient un attribut contextuel.
	 */
	boolean containsKey(final String key);

	/**
	 * Permet d'enregistrer un objet dans le contexte <ul>
	 * <li>les DtObject doivent imp�rativement commencer par DTO_</li>
	 * <li>les DtCollection doivent imp�rativement commencer par DTC_</li>
	 * <ul>
	 * <br>
	 * <b>Attention</b> � la diff�rence des maps la valeur ins�r�e doit �tre non null !
	 * @param key Cl� d'enregistrement de l'objet
	 * @param value Objet � enregistrer (non null)
	 */
	void put(final String key, final Object value);

	/**
	 * @return Si aucun param�tre n'a �t� sett� dans cet �venement (uniquement dans le cas d'un Kevent non initialis�)
	 */
	boolean isEmpty();
}
