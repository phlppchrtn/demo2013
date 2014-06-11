package rest;

import io.vertigo.kernel.lang.Assertion;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Session d'un utilisateur.
 * <br/>
 * Un utilisateur
 * <ul>
 * <li>est authentifi� ou non,</li>
 * <li>poss�de une liste de roles (pr�alablement enregistr�s dans la RoleRegistry),</li>
 * <li>poss�de une liste d'attributs s�rialisables</li>.
 * </ul>
 *
 * @author alauthier, pchretien
 * @version $Id: AbstractUserSession.java,v 1.14 2010/11/16 10:36:47 pchretien Exp $
 */
public abstract class AbstractUserSession implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 467617818948129397L;

	/**
	 * Map des attributs de la session utilisateur.
	 */
	private final Map<String, Serializable> attributes = new HashMap<>();

	/**
	 * Indique si l'utilisateur est authentifi�.
	 * Par d�faut l'utilisateur n'est pas authentifi�.
	 */
	private boolean authenticated;

	//===========================================================================
	//=======================GESTION DES AUTHENTIFICATIONS=======================
	//===========================================================================
	/**
	 * Indique si l'utilisateur est authentifi�.
	 * L'authentification est act�e par l'appel de la m�thode <code>authenticate()</code>
	 *
	 * @return boolean Si l'utilisateur s'est authentifi�.
	 */
	public final boolean isAuthenticated() {
		return authenticated;
	}

	/**
	 * M�thode permettant d'indiquer que l'utilisateur est authentifi�.
	 */
	public final void authenticate() {
		authenticated = true;
	}

	//===========================================================================
	//=======================GESTION DES ATTRIBUTS===============================
	//===========================================================================
	/**
	 * Retourne un attribut de la session, �ventuellement null si non trouv�.
	 *
	 * @param key Clef d'enregistrement.
	 * @return Objet ou null si aucune correspondance avec la clef
	 */
	public final Serializable getAttribute(final String key) {
		Assertion.checkNotNull(key);
		// ----------------------------------------------------------------------
		return attributes.get(key);
	}

	/**
	 * Ajoute ou modifie un attribut de la session.
	 *
	 * @param key Clef d'enregistrement.
	 * @param value Object � enregistrer.
	 * @return Valeur pr�c�dente (null si aucune)
	 */
	public final Serializable putAttribute(final String key, final Serializable value) {
		Assertion.checkNotNull(key);
		Assertion.checkNotNull(value);
		// ----------------------------------------------------------------------
		return attributes.put(key, value);
	}

	/**
	 * Supprime un attribut de la session.
	 *
	 * @param key Clef d'enregistrement
	 */
	public final void removeAttribute(final String key) {
		Assertion.checkNotNull(key);
		// ----------------------------------------------------------------------
		attributes.remove(key);
	}

	/**
	 * Gestion multilingue.
	 * Local associ�e � l'utilisateur.
	 * @return Locale associ�e � l'utilisateur.
	 */
	public abstract Locale getLocale();
}
