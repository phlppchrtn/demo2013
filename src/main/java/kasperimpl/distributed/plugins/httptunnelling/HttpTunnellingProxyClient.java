package kasperimpl.distributed.plugins.httptunnelling;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.Assertion;

/**
 * Proxy http pour communication http au lieu de rmi.
 *
 * @author pchretien
 */
final class HttpTunnellingProxyClient<F> implements InvocationHandler {
	private final String serverURL;
	private final String address;
	private final Class<F> beanClass;
	private String cookie;

	/**
	 * Constructeur.
	 */
	HttpTunnellingProxyClient(final String serverURL, final Class<F> beanClass, final String address) {
		Assertion.notNull(serverURL, "URL non renseignée");
		Assertion.notNull(beanClass, "Classe du Bean non renseignée");
		Assertion.notNull(address, "Adresse du Bean non renseignée");
		//---------------------------------------------------------------------
		this.serverURL = serverURL;
		this.beanClass = beanClass;
		this.address = address;
	}

	/**
	 * Ouvre la connection http.
	 * @param serverURL java.lang.String
	 * @param methodName java.lang.String
	 * @return java.net.URLConnection
	 * @throws java.io.IOException   Exception de communication
	 */
	private static URLConnection openConnection(final String serverURL, final String address, final String methodName) throws IOException {
		// gzip=false pour indiquer à un éventuel filtre http de compression
		// que le flux est déjà compressé par HttpProxyClient et HttpProxyServer
		//System.out.println(">>>url= " + serverURL + '/' + address + '/' + methodName + "?gzip=false");
		final URL url = new URL(serverURL + '/' + address + '/' + methodName + "?gzip=false");
		final URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type", java.awt.datatransfer.DataFlavor.javaSerializedObjectMimeType);
		connection.setRequestProperty("Accept-Encoding", "gzip");
		connection.setConnectTimeout(60 * 1000);
		connection.setReadTimeout(10 * 60 * 1000);

		return connection;
	}

	//==========================================================================
	//==========================================================================
	//==========================================================================

	/** {@inheritDoc} */
	public Object invoke(final Object proxy, final Method method, final Object[] parameters) throws Throwable {
		Assertion.notNull(proxy);
		Assertion.notNull(method);
		//Les méthodes sans paramètres envoient null
		//---------------------------------------------------------------------
		final Object result = doInvoke(method, parameters == null ? new Object[0] : parameters);
		if (result instanceof Throwable) {
			throw (Throwable) result;
		}
		return result;
	}

	private Object doInvoke(final Method method, final Object[] parameters) throws IOException {
		final URLConnection connection = openConnection(serverURL, address, method.getName());
		final HttpTunnellingRequest request = new HttpTunnellingRequest(beanClass.getName(), method.getName(), method.getParameterTypes(), parameters);
		HttpTunnellingReaderWriterUtil.write(request, connection.getOutputStream());

		if (cookie != null) {
			// on met le cookie ici pour pouvoir assurer si besoin un suivi de session par Cookie dans le load-balancer
			connection.setRequestProperty("Cookie", cookie);
		}

		connection.connect();

		final String setCookie = connection.getHeaderField("Set-Cookie");
		if (setCookie != null) {
			cookie = setCookie;
		}

		return read(connection);
	}

	/**
	 * Lit l'objet renvoyé dans le flux de réponse.
	 * Il y a une spécificité sur la connexion qui peut avoir un flux d'erreur qu'il faut fermer.
	 * @return java.lang.Object
	 * @param connection java.net.URLConnection
	 * @throws java.io.IOException   Exception de communication
	 */
	private static Object read(final URLConnection connection) throws IOException {
		try {
			return HttpTunnellingReaderWriterUtil.read(connection.getInputStream());
		} catch (final ClassNotFoundException e) {
			//Une classe transmise par le serveur n'a pas été trouvée
			throw new KRuntimeException("Une classe transmise par le serveur n'a pas été trouvée", e);
		} finally {
			// ce close doit être fait en finally
			// (http://java.sun.com/j2se/1.5.0/docs/guide/net/http-keepalive.html)
			if (connection instanceof HttpURLConnection) {
				final InputStream error = ((HttpURLConnection) connection).getErrorStream();
				if (error != null) {
					error.close();
				}
			}
		}
	}
}
