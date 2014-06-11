package rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author npiedeloup
 * @version $Id: Controller.java,v 1.21 2010/11/16 10:36:47 pchretien Exp $
 */
public interface Controller extends UIController {
	/**
	 * @return Contexte du controller
	 */
	Context getContext();

	/**
	 * @return Structure des erreurs relatives à la page
	 */
	ErrorProcess getErrorPage();

	//	/**
	//	 * @return Evénement initial
	//	 */
	//	UIEvent getEvent();

	/**
	 * @return Request HTTP courante
	 */
	HttpServletRequest getCurrentRequest();

	/**
	 * @return Response HTTP courante
	 */
	HttpServletResponse getCurrentResponse();

}
