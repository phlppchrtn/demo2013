package rest;

import io.vertigo.kernel.Home;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.UIManager;

/**
 * Servlet de base pour toutes les applications Kasper. Elle prend en charge toutes les requêtes entrantes.
 * 
 * @author alauthier
 * @version $Id: AbstractControllerServlet.java,v 1.16 2011/02/18 09:41:48 npiedeloup Exp $
 * @param <A> ApplicationState de la servlet
 * @param <U> Utilisateur de la servlet
 */
public abstract class MyServlet<A extends AbstractApplicationState, U extends AbstractUserSession> extends HttpServlet implements UserSessionFactory<U> {
	private static final String EXTERNAL_PROPERTIES_PARAM_NAME = "external-properties";
	private static final long serialVersionUID = -8573496644849545507L;
	private RequestHandler requestHandler;
	private final ServletListener servletListener = createServletListener();

	/** {@inheritDoc} */
	@Override
	public final void init(final ServletConfig config) throws ServletException {
		super.init(config);
		try {
			// Création de l'état de l'application
			final ApplicationState applicationState = createApplicationState();
			final Properties conf = createProperties();
			// Initialisation de l'état de l'application
			final Boot boot = new Boot();
			boot.init(applicationState, conf);

			requestHandler = createRequestHandler(this);
			// Notification de l'événement de démarrage de la servlet
			servletListener.onServletStart(getClass().getName());
		} catch (final Exception e) {
			// Dans cette méthode on doit retourner uniquement des ServletException
			e.printStackTrace();
			throw new ServletException(e);
		} catch (final Error e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Création des propriétés à partir des différents fichiers de configuration.
	 * - Web XML 
	 * - Fichier externe  défini par la valeur de la propriété système : external-properties
	 * 
	 * @return Properties
	 * @throws IOException Erreur de lecture du fichier de configuration
	 */
	private Properties createProperties() throws IOException {
		// ======================================================================
		// ===Conversion en Properties du fichier de paramétrage de la servlet===
		// ======================================================================
		final Properties servletParams = new Properties();
		String name;

		/*
		 * On récupère les paramètres de la servlet. (web.xml)
		 */
		for (final Enumeration<String> enumeration = getServletConfig().getInitParameterNames(); enumeration.hasMoreElements();) {
			name = enumeration.nextElement();
			servletParams.put(name, getServletConfig().getInitParameter(name));
		}

		/*
		 * On récupère les paramètres du context (web.xml ou fichier tomcat par exemple) Ces paramètres peuvent surcharger
		 * les paramètres de la servlet de façon à créer un paramétrage adhoc de développement par exemple.
		 */
		for (final Enumeration<String> enumeration = getServletContext().getInitParameterNames(); enumeration.hasMoreElements();) {
			name = enumeration.nextElement();
			servletParams.put(name, getServletContext().getInitParameter(name));
		}

		/*
		 * On récupère les paramètres du fichier de configuration externe (-Dexternal-properties).
		 * Ces paramètres peuvent surcharger les paramètres de la servlet de façon à créer un paramétrage adhoc de développement par exemple.
		 */
		final String externalPropertiesFileName = System.getProperty(EXTERNAL_PROPERTIES_PARAM_NAME);
		if (externalPropertiesFileName != null) {
			final InputStream inputStream = new FileInputStream(externalPropertiesFileName);
			try {
				servletParams.load(inputStream);
			} finally {
				inputStream.close();
			}
		}

		return servletParams;
	}

	/** {@inheritDoc} */
	@Override
	public final void destroy() {
		servletListener.onServletDestroy(getClass().getName());
		Home.stop();
		super.destroy();
	}

	/** {@inheritDoc} */
	@Override
	public final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
		requestHandler.executeRequest(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
		requestHandler.executeRequest(request, response);
	}

	/**
	 * Création de l'état de l'application. Cette méthode est invoquée automatiquement à l'initialisation de
	 * l'application.
	 * 
	 * @return Etat de l'application
	 */
	protected abstract A createApplicationState();

	private ServletListener createServletListener() {
		return new ServletListenerImpl();
	}

	/**
	 * Initialise la classe avec le fichier de paramétrage.
	 * @param userSessionFactory Factory de UserSession
	 * @throws Exception erreur
	 */
	private static final <U extends AbstractUserSession> RequestHandler createRequestHandler(final UserSessionFactory<U> userSessionFactory) throws Exception {
		//On enregistre le RequestHandler sur UI
		final UIManagerImpl uiManagerImpl = (UIManagerImpl) Home.getManager(UIManager.class);
		final RequestHandler requestHandler = new RequestHandlerImpl<U>(uiManagerImpl, Home.getManager(KTransactionManager.class), Home.getManager(KSecurityManager.class), userSessionFactory);
		uiManagerImpl.registerRequestHandler(requestHandler);
		return requestHandler;
	}
}
