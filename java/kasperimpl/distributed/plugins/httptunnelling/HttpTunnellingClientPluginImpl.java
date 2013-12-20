package kasperimpl.distributed.plugins.httptunnelling;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javax.inject.Inject;
import javax.inject.Named;

import kasper.distributed.ClientPlugin;
import kasper.kernel.util.Assertion;

/**
 * @author pchretien
 * @version $Id: HttpTunnellingClientPluginImpl.java,v 1.2 2012/05/25 13:33:35 pchretien Exp $
 */
public final class HttpTunnellingClientPluginImpl implements ClientPlugin {
	private final String serverURL;

	@Inject
	public HttpTunnellingClientPluginImpl(@Named("serverURL") final String serverURL) {
		Assertion.notEmpty(serverURL);
		//---------------------------------------------------------------------
		this.serverURL = serverURL;
	}

	public <F> F createProxy(final Class<F> facadeClass, final String address) {
		final InvocationHandler proxy = new HttpTunnellingProxyClient(serverURL, facadeClass, address);
		return (F) Proxy.newProxyInstance(proxy.getClass().getClassLoader(), new Class[] { facadeClass }, proxy);
	}
}
