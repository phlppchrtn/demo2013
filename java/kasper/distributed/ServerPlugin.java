package kasper.distributed;

import kasper.kernel.manager.Plugin;

/**
 * Résolution des instances de services à partir de leur nom logique. 
 * 
 * @author pchretien
 * @version $Id: ServerPlugin.java,v 1.1 2012/03/26 13:24:46 pchretien Exp $
 */
public interface ServerPlugin extends Plugin {
	/**
	 * Résolution des services.
	 * @param address Adresse logique du service
	 * @return Objet implémentant les services.  
	 */
	Object lookUp(final String address);
}
