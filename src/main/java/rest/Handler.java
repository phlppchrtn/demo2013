package rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Element atomique du traitement des requêtes HTTP.
 * 
 * @author pchretien, npiedeloup
 * @version $Id: Handler.java,v 1.4 2010/11/16 10:36:49 pchretien Exp $
 */
public interface Handler {

	/**
	 * Effectue le traitement associé à ce Handler.
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param requestContext Context courant du traitement de cette request
	 * @param chain Chaine de traitement encours.
	 * @throws Exception Erreur de traitement
	 */
	void doFilter(final HttpServletRequest request, final HttpServletResponse response, final RequestContext requestContext, final HandlerChain chain) throws Exception;
}
