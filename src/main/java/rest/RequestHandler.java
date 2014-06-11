package rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Traitement des requ�tes HTTP.
 * 
 * @author pchretien, npiedeloup
 * @version $Id: RequestHandler.java,v 1.6 2010/11/16 10:36:47 pchretien Exp $
 */
public interface RequestHandler {
	/**
	 * M�thode invoqu�e lors d'un appel � la servlet.
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException Erreur d'entr�e/sortie
	 * @throws ServletException Si erreur.
	 */
	void executeRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException;

	/**
	 * @return UserInfoHandler
	 */
	<U extends AbstractUserSession> UserInfoHandler<U> getUserInfoHandler();
}
