package kasperimpl.distributed.plugins.httptunnelling;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kasper.distributed.DistributedManager;
import kasper.distributed.ServerPlugin;
import kasper.kernel.util.Assertion;
import kasper.kernel.util.ClassUtil;

/**
 * Handler HTTP pour communication Java sur http (et non par rmi).
 *
 * @author pchretien
 * @version $Id: HttpTunnellingHandler.java,v 1.2 2013/05/28 17:17:45 pchretien Exp $
 */
public final class HttpTunnellingHandler {
	/**
	 * R�ceptionne une requ�te envoy�e par HttpProxyClient puis ex�cute la m�thode demand�e
	 * sur la fa�ade demand�e. La s�curit� doit �tre v�rifi�e par la fa�ade et/ou un filter http.
	 * Les arguments Java de la m�thode sont pass�es dans la request et le r�sultat �ventuel
	 * est envoy� dans la response.
	 * @param req Request HTTP
	 * @param res HttpServletResponse
	 * @throws IOException Erreur d'entr�e/sortie
	 */
	public void execute(final DistributedManager distributedManager, final HttpServletRequest req, final HttpServletResponse res) throws IOException {
		Assertion.notNull(distributedManager);
		Assertion.notNull(req);
		Assertion.notNull(res);
		//---------------------------------------------------------------------	
		Object result;
		try {
			final HttpTunnellingRequest request = (HttpTunnellingRequest) HttpTunnellingReaderWriterUtil.read(req.getInputStream());
			result = execute(distributedManager.getServerPlugin(), request);
		} catch (final Throwable throwable) {
			result = throwable;
		}

		HttpTunnellingReaderWriterUtil.write(result, res.getOutputStream());
	}

	private static Object execute(final ServerPlugin serverPlugin, final HttpTunnellingRequest request) throws Throwable {
		final Object bean = serverPlugin.lookUp(request.getAddress());
		final String methodName = request.getOperation();
		final Object[] parameters = request.getParameters();
		final Class<?>[] parameterTypes = request.getParameterTypes();

		final Method method = ClassUtil.findMethod(bean.getClass(), methodName, parameterTypes);

		return invokeMethod(bean, method, parameters);
	}

	/**
	 * Invoque une m�thode par r�flection sur l'objet sp�cifi� et avec les arguments de m�thode sp�cifi�s.
	 * Si une InvocationTargetException survient, l'exception d'origine dans la m�thode appel�e est d�sencapsul�e.
	 */
	private static Object invokeMethod(final Object object, final Method method, final Object[] args) throws Throwable, IllegalAccessException {
		Assertion.notNull(object);
		Assertion.notNull(method);
		//---------------------------------------------------------------------	
		try {
			//System.out.println("invoke " + method.getName() + " on " + object.getClass().getName());
			return method.invoke(object, args);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}
}
