package kasperimpl.distributed;

import javax.inject.Inject;

import kasper.distributed.ClientPlugin;
import kasper.distributed.DistributedManager;
import kasper.distributed.ServerPlugin;
import kasper.kernel.lang.Option;
import kasper.kernel.util.Assertion;

/**
 * Implémentation de référence de la gestion des appels distribués.
 * 
 * @author pchretien
 */
public final class DistributedManagerImpl implements DistributedManager {
	@Inject
	private Option<ClientPlugin> clientPlugin;
	@Inject
	private Option<ServerPlugin> serverPlugin;

	/** {@inheritDoc} */
	public ClientPlugin getClientPlugin() {
		Assertion.isDefined(clientPlugin, "Aucun plugin client déclaré");
		//---------------------------------------------------------------------
		return clientPlugin.get();
	}

	/** {@inheritDoc} */
	public ServerPlugin getServerPlugin() {
		Assertion.isDefined(serverPlugin, "Aucun plugin serveur déclaré");
		//---------------------------------------------------------------------
		return serverPlugin.get();
	}
}
