package rest;

/**
 * Interface de réception des  événements produits par la servlet.
 *
 * @author pchretien
 * @version $Id: ServletListener.java,v 1.4 2010/11/16 10:36:47 pchretien Exp $
 */
public interface ServletListener {
	/**
	 * Evénement remonté lors du démarrage de la servlet.
	 * @param servletName Nom de la servlet
	 */
	void onServletStart(String servletName);

	/**
	 * Evénement remonté lors de l'arrêt de la servlet.
	 * @param servletName Nom de la servlet
	 */
	void onServletDestroy(String servletName);
}
