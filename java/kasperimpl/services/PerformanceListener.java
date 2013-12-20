package kasperimpl.services;

/**
 * Listener des événemnts envoyés par le proxy de performance.
 * 
 * @author pchretien
 * @version $Id: PerformanceListener.java,v 1.1 2012/03/23 13:22:03 pchretien Exp $
 */
interface PerformanceListener {
	/**
	 * Start.
	 * @param processName Nom du process
	 */
	void start(String processName);

	/**
	 * Handler d'erreur.
	 * @param throwable Exception
	 */
	void onException(final Throwable throwable);

	/**
	 * Stop.
	 */
	void stop();
}
