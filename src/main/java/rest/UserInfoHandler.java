package rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Traitement des requêtes HTTP.
 * 
 * @author pchretien, npiedeloup
 * @version $Id: UserInfoHandler.java,v 1.4 2010/11/16 10:36:47 pchretien Exp $
 * @param <U> Class de l'objet Session Utilisateur
 */
public interface UserInfoHandler<U extends AbstractUserSession> {
	/**
	 * @param session Session HTTP
	 * @return Si la session correspond a un utilisateur connecté. (Donc valide)
	 */
	boolean hasUserSession(final HttpSession session);

	/**
	 * Remplace la session existante.
	 * 
	 * @param request Request
	 * @return Nouvelle session
	 */
	U resetUserSession(final HttpServletRequest request);
}
