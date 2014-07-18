package kasper.distributed;

import kasper.kernel.manager.Manager;

/** 
 * Gestion des appels distribu�s.
 * Ce module est utilis� 
 *  - soit en mode client 
 *  - soit en mode server
 *  - soit en mode mixte
 *  
 * @author pchretien
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
