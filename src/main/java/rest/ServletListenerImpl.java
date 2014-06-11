package rest;

import org.apache.log4j.Logger;

/**
 * Implémentation du listener des événements produits par la servlet.
 *
 * @author pchretien
 * @version $Id: ServletListenerImpl.java,v 1.7 2010/11/16 10:36:47 pchretien Exp $
 */
final class ServletListenerImpl implements ServletListener {

	/**
	 * Mécanisme de log racine
	 */
	private final Logger generalLog;

	/**
	 * Constructeur.
	 */
	ServletListenerImpl() {
		generalLog = Logger.getRootLogger();
	}

	// --------------------------------------------------------------------------

	/** {@inheritDoc} */
	public void onServletStart(final String servletName) {
		if (generalLog.isInfoEnabled()) {
			generalLog.info("Start servlet " + servletName);
		}
	}

	/** {@inheritDoc} */
	public void onServletDestroy(final String servletName) {
		if (generalLog.isInfoEnabled()) {
			generalLog.info("Destroy servlet " + servletName);
		}
	}
}
