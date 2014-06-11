package rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Chaine de traitement des requ�tes HTTP.
 * 
 * @author npiedeloup
 * @version $Id: HandlerChain.java,v 1.4 2010/11/16 10:36:49 pchretien Exp $
 */
public interface HandlerChain {

	/**
	 * Effectue le traitement associ� � ce Handler.
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param requestContext Context courant du traitement de cette request
	 * @throws Exception Erreur de traitement
	 */
	void doFilter(final HttpServletRequest request, final HttpServletResponse response, final RequestContext requestContext) throws Exception;
}
