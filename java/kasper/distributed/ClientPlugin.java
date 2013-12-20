package kasper.distributed;

import kasper.kernel.manager.Plugin;

/** 
 * Plugin permettant d'invoquer un service distribué.
 * Tout service doit être contractualisé sous la forme d'une interface.
 * Un proxy est alors créé.
 *   
 * @author pchretien
 * @version $Id: ClientPlugin.java,v 1.1 2012/03/26 13:24:46 pchretien Exp $
 */
public interface ClientPlugin extends Plugin {
	/**
	 * Création d'un proxy client. 
	 * Le proxy permet de distribuer des services ; ces services sont déclarés dans une interface et déployés sur un serveur.  
	 * 
	 * @param <F> Type de l'interface à distribuer
	 * @param address Adresse logique du service
	 * @return Interface cliente du service
	 */
	<F> F createProxy(final Class<F> facadeClass, final String address);
}
