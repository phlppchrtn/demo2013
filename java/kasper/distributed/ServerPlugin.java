package kasper.distributed;

import kasper.kernel.manager.Plugin;

/**
 * R�solution des instances de services � partir de leur nom logique. 
 * 
 * @author pchretien
 * @version $Id: ServerPlugin.java,v 1.1 2012/03/26 13:24:46 pchretien Exp $
 */
public interface ServerPlugin extends Plugin {
	/**
	 * R�solution des services.
	 * @param address Adresse logique du service
	 * @return Objet impl�mentant les services.  
	 */
	Object lookUp(final String address);
}
