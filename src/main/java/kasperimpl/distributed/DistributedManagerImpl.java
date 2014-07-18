package kasperimpl.distributed;

import javax.inject.Inject;

import kasper.distributed.ClientPlugin;
import kasper.distributed.DistributedManager;
import kasper.distributed.ServerPlugin;
import kasper.kernel.lang.Option;
import kasper.kernel.util.Assertion;

/**
 * Impl�mentation de r�f�rence de la gestion des appels distribu�s.
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
		Assertion.isDefined(clientPlugin, "Aucun plugin client d�clar�");
		//---------------------------------------------------------------------
		return clientPlugin.get();
	}

	/** {@inheritDoc} */
	public ServerPlugin getServerPlugin() {
		Assertion.isDefined(serverPlugin, "Aucun plugin serveur d�clar�");
		//---------------------------------------------------------------------
		return serverPlugin.get();
	}
}
