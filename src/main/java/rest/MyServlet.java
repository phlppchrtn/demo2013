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
 * Servlet de base pour toutes les applications Kasper. Elle prend en charge toutes les requ�tes entrantes.
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
			// Cr�ation de l'�tat de l'application
			final ApplicationState applicationState = createApplicationState();
			final Properties conf = createProperties();
			// Initialisation de l'�tat de l'application
			final Boot boot = new Boot();
			boot.init(applicationState, conf);

			requestHandler = createRequestHandler(this);
			// Notification de l'�v�nement de d�marrage de la servlet
			servletListener.onServletStart(getClass().getName());
		} catch (final Exception e) {
			// Dans cette m�thode on doit retourner uniquement des ServletException
			e.printStackTrace();
			throw new ServletException(e);
		} catch (final Error e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Cr�ation des propri�t�s � partir des diff�rents fichiers de configuration.
	 * - Web XML 
	 * - Fichier externe  d�fini par la valeur de la propri�t� syst�me : external-properties
	 * 
	 * @return Properties
	 * @throws IOException Erreur de lecture du fichier de configuration
	 */
	private Properties createProperties() throws IOException {
		// ======================================================================
		// ===Conversion en Properties du fichier de param�trage de la servlet===
		// ======================================================================
		final Properties servletParams = new Properties();
		String name;

		/*
		 * On r�cup�re les param�tres de la servlet. (web.xml)
		 */
		for (final Enumeration<String> enumeration = getServletConfig().getInitParameterNames(); enumeration.hasMoreElements();) {
			name = enumeration.nextElement();
			servletParams.put(name, getServletConfig().getInitParameter(name));
		}

		/*
		 * On r�cup�re les param�tres du context (web.xml ou fichier tomcat par exemple) Ces param�tres peuvent surcharger
		 * les param�tres de la servlet de fa�on � cr�er un param�trage adhoc de d�veloppement par exemple.
		 */
		for (final Enumeration<String> enumeration = getServletContext().getInitParameterNames(); enumeration.hasMoreElements();) {
			name = enumeration.nextElement();
			servletParams.put(name, getServletContext().getInitParameter(name));
		}

		/*
		 * On r�cup�re les param�tres du fichier de configuration externe (-Dexternal-properties).
		 * Ces param�tres peuvent surcharger les param�tres de la servlet de fa�on � cr�er un param�trage adhoc de d�veloppement par exemple.
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
	 * Cr�ation de l'�tat de l'application. Cette m�thode est invoqu�e automatiquement � l'initialisation de
	 * l'application.
	 * 
	 * @return Etat de l'application
	 */
	protected abstract A createApplicationState();

	private ServletListener createServletListener() {
		return new ServletListenerImpl();
	}

	/**
	 * Initialise la classe avec le fichier de param�trage.
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
