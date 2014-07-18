package kasper.distributed;

import kasper.kernel.manager.Plugin;

/**
 * R�solution des instances de services � partir de leur nom logique. 
 * 
 * @author pchretien
 */
public interface ServerPlugin extends Plugin {
	/**
	 * R�solution des services.
	 * @param address Adresse logique du service
	 * @return Objet impl�mentant les services.  
	 */
	Object lookUp(final String address);
}
