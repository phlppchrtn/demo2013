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
	 * Réceptionne une requête envoyée par HttpProxyClient puis exécute la méthode demandée
	 * sur la façade demandée. La sécurité doit être vérifiée par la façade et/ou un filter http.
	 * Les arguments Java de la méthode sont passées dans la request et le résultat éventuel
	 * est envoyé dans la response.
	 * @param req Request HTTP
	 * @param res HttpServletResponse
	 * @throws IOException Erreur d'entrée/sortie
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
	 * Invoque une méthode par réflection sur l'objet spécifié et avec les arguments de méthode spécifiés.
	 * Si une InvocationTargetException survient, l'exception d'origine dans la méthode appelée est désencapsulée.
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
