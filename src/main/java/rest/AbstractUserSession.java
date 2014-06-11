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
 * <li>est authentifié ou non,</li>
 * <li>possède une liste de roles (préalablement enregistrés dans la RoleRegistry),</li>
 * <li>possède une liste d'attributs sérialisables</li>.
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
	 * Indique si l'utilisateur est authentifié.
	 * Par défaut l'utilisateur n'est pas authentifié.
	 */
	private boolean authenticated;

	//===========================================================================
	//=======================GESTION DES AUTHENTIFICATIONS=======================
	//===========================================================================
	/**
	 * Indique si l'utilisateur est authentifié.
	 * L'authentification est actée par l'appel de la méthode <code>authenticate()</code>
	 *
	 * @return boolean Si l'utilisateur s'est authentifié.
	 */
	public final boolean isAuthenticated() {
		return authenticated;
	}

	/**
	 * Méthode permettant d'indiquer que l'utilisateur est authentifié.
	 */
	public final void authenticate() {
		authenticated = true;
	}

	//===========================================================================
	//=======================GESTION DES ATTRIBUTS===============================
	//===========================================================================
	/**
	 * Retourne un attribut de la session, éventuellement null si non trouvé.
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
	 * @param value Object à enregistrer.
	 * @return Valeur précédente (null si aucune)
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
	 * Local associée à l'utilisateur.
	 * @return Locale associée à l'utilisateur.
	 */
	public abstract Locale getLocale();
}
