package rest;

/**
 * Interface de r�ception des  �v�nements produits par la servlet.
 *
 * @author pchretien
 * @version $Id: ServletListener.java,v 1.4 2010/11/16 10:36:47 pchretien Exp $
 */
public interface ServletListener {
	/**
	 * Ev�nement remont� lors du d�marrage de la servlet.
	 * @param servletName Nom de la servlet
	 */
	void onServletStart(String servletName);

	/**
	 * Ev�nement remont� lors de l'arr�t de la servlet.
	 * @param servletName Nom de la servlet
	 */
	void onServletDestroy(String servletName);
}
