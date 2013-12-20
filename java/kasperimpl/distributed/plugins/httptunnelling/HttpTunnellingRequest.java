package kasperimpl.distributed.plugins.httptunnelling;

import java.io.Serializable;

import kasper.kernel.util.Assertion;

/**
 * Objet utilisé dans le protocole de distribution.
 * Un service est identifié par 
 * - son adresse
 * - le nom de l'operation 
 * - les paramètres 
 *
 * Le nom de l'operation sera par exemple tiré du nom de la méthode java.
 * 
 * @author pchretien
 * @version $Id: HttpTunnellingRequest.java,v 1.2 2013/05/28 17:17:45 pchretien Exp $
 */
final class HttpTunnellingRequest implements Serializable {
	private static final long serialVersionUID = -1L;

	private final String address;
	private final String operation;
	private final Class<?>[] parameterTypes;
	private final Object[] parameters;

	/**
	 * Constructeur.	
	 */
	HttpTunnellingRequest(final String address, final String operation, final Class<?>[] parameterTypes, final Object[] parameters) {
		Assertion.notEmpty(address);
		Assertion.notEmpty(operation);
		Assertion.notNull(parameterTypes);
		Assertion.notNull(parameters);
		Assertion.precondition(parameters.length == parameterTypes.length, "les tabeaux doivent avoir une même taille");
		//---------------------------------------------------------------------
		this.address = address;
		this.operation = operation;
		this.parameterTypes = parameterTypes;
		this.parameters = parameters;
	}

	String getAddress() {
		return address;
	}

	Object[] getParameters() {
		return parameters;
	}

	String getOperation() {
		return operation;
	}

	Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
}
