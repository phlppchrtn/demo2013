package rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Traitement des requêtes HTTP.
 * 
 * @author pchretien, npiedeloup
 * @version $Id: RequestHandler.java,v 1.6 2010/11/16 10:36:47 pchretien Exp $
 */
public interface RequestHandler {
	/**
	 * Méthode invoquée lors d'un appel à la servlet.
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException Erreur d'entrée/sortie
	 * @throws ServletException Si erreur.
	 */
	void executeRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException;

	/**
	 * @return UserInfoHandler
	 */
	<U extends AbstractUserSession> UserInfoHandler<U> getUserInfoHandler();
}
