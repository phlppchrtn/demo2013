package rest;

import kasper.domain.model.DtObject;

/**
 * Contexte d'une page.
 * Ce contexte (lourd) est utilisé pour : <ul>
 * <li>Exécuter les actions dans les contrôleurs,</li>
 * <li>Afficher les données dans les pages JSP, export CSV...</li>
 * </ul>
 *
 * Le contexte sait se sérialiser sous la forme d'une chaine de caractères ;
 * une fois la sérialisation effectuée le contexte est non modifiable.
 *
 * Le contexte présente une API très comparable à celle de Map à la différence suivante :<br>
 * La méthode put n'admet pas de valeur null, corrolaire les get retournent nécessairement un objet non null.
 *
 * @author  ovannetzel
 * @version $Id: Context.java,v 1.8 2010/11/16 10:36:49 pchretien Exp $
 */
public interface Context {
	/**
	 * Retourne un object enregistré dans le context du controller.<br>
	 * Cet objet est non null.
	 * @param <J> Type java de l'objet recherché
	 * @param key Clé d'enregistrement de l'objet
	 * @return Object enregistré.
	 */
	<J> J get(final String key);

	/**
	 * Accesseur typé.
	 * @param key Clé d'enregistrement de l'objet
	 * @return Objet enregistré.
	 */
	String getString(final String key);

	/**
	 * Accesseur typé.
	 * @param <D> Type de DtObject
	 * @param key Clé d'enregistrement de l'objet
	 * @return Input enregistré.
	 */
	<D extends DtObject> DtObjectInput<D> getDtObjectInput(final String key);

	/**
	 * Accesseur typé.
	 * @param <D> Type de DtObject
	 * @param key Clé d'enregistrement de l'objet
	 * @return Input enregistré.
	 */
	<D extends DtObject> DtCollectionInput<D> getDtCollectionInput(final String key);

	/**
	 * Accesseur typé.
	 * @param key Clé d'enregistrement de l'objet
	 * @return Objet enregistré.
	 */
	Long getLong(final String key);

	/**
	 * Accesseur typé.
	 * @param key Clé d'enregistrement de l'objet
	 * @return Objet enregistré.
	 */
	Integer getInteger(final String key);

	/**
	 * Accesseur typé.
	 * @param key Clé d'enregistrement de l'objet
	 * @return Objet enregistré.
	 */
	Boolean getBoolean(final String key);

	/**
	 * @param key Clé d'enregistrement de l'objet
	 * @return Si le controller contient un attribut contextuel.
	 */
	boolean containsKey(final String key);

	/**
	 * Permet d'enregistrer un objet dans le contexte <ul>
	 * <li>les DtObject doivent impérativement commencer par DTO_</li>
	 * <li>les DtCollection doivent impérativement commencer par DTC_</li>
	 * <ul>
	 * <br>
	 * <b>Attention</b> à la différence des maps la valeur insérée doit être non null !
	 * @param key Clé d'enregistrement de l'objet
	 * @param value Objet à enregistrer (non null)
	 */
	void put(final String key, final Object value);

	/**
	 * @return Si aucun paramètre n'a été setté dans cet évenement (uniquement dans le cas d'un Kevent non initialisé)
	 */
	boolean isEmpty();
}
