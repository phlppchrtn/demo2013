package kasper.distributed;

import kasper.kernel.manager.Manager;

/** 
 * Gestion des appels distribués.
 * Ce module est utilisé 
 *  - soit en mode client 
 *  - soit en mode server
 *  - soit en mode mixte
 *  
 * @author pchretien
 * @version $Id: DistributedManager.java,v 1.1 2012/03/26 13:24:46 pchretien Exp $
 */
public interface DistributedManager extends Manager {
	/**
	 * @return Plugin client. (celui qui consomme les services)
	 */
	ClientPlugin getClientPlugin();

	/**
	 * @return Plugin server. (celui qui produit les services)
	 */
	ServerPlugin getServerPlugin();
}
