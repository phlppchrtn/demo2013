package kraft;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import kasper.kernel.Home;
import kasper.kernel.configurator.HomeConfig;
import kasper.kernel.configurator.xml.XMLHomeConfigBuilder;
import kasper.locale.LocaleManager;

/**
 * Charge l'environnement de test par defaut.
 * 
 * @author pchretien
 */
final class KasperBridge {
	final void start() throws Exception {
		// Cr�ation de l'�tat de l'application
		// Initialisation de l'�tat de l'application
		final URL xmlURL = KraftBoot.doResolve("managers-kraft.xml", "/kraft/managers-kraft.xml");
		final Properties properties = loadProperties();
		final HomeConfig homeConfig = new XMLHomeConfigBuilder(xmlURL, properties, false).build();
		Home.start(homeConfig);

		Home.getContainer().getManager(LocaleManager.class).add("kasperx.domain.constraint.Constraint", kasperx.domain.constraint.Resources.values());
		Home.getContainer().getManager(LocaleManager.class).add("kasperx.domain.formatter.Formatter", kasperx.domain.formatter.Resources.values());

	}

	final void stop() throws Exception {
		Home.stop();
	}

	/**
	 * Charge le fichier properties. Par defaut vide, mais il peut-�tre
	 * surcharg�.
	 * 
	 * @return properties utilis�s pour d�marrer les managers.
	 * @throws FileNotFoundException
	 */
	private Properties loadProperties() throws IOException {
		final Properties properties = new Properties();
		URL propertiesURL = KraftBoot.doResolve("kraft.properties", "default-kraft.properties");
		final InputStream in = propertiesURL.openStream();
		try {
			properties.load(in);
		} finally {
			in.close();
		}
		return properties;
	}
}
