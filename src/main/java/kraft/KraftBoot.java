package kraft;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.Assertion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class KraftBoot {
	private static KraftServer server;

	public static void main(final String[] args) throws Exception {
		Config config = createConfig();
		server = new KraftServer(config);
		server.start();
		System.in.read();
		server.stop();
	}

	private static Config createConfig() {
		return createConfig("config.json", "default-config.json");
	}

	/**
	 * Création d'un objet Java Properties à partir d'un nom de fichier.
	 * @param fileName Nom du fichier.
	 * @return Properties
	 */
	private static Config createConfig(final String fileName, final String defaultFileName) {
		try {
			return doCreateConfig(fileName, defaultFileName);
		} catch (final IOException e) {
			throw new KRuntimeException("Impossible de récupérer le fichier de configuration.", e);
		}
	}

	private static Config doCreateConfig(final String fileName, final String defaultFileName) throws IOException {
		Assertion.notNull(fileName);
		// ---------------------------------------------------------------------
		Reader reader = new InputStreamReader(doResolve(fileName, defaultFileName).openStream());
		try {
			Gson gson = new GsonBuilder().create();
			return gson.fromJson(reader, Config.class);
		} finally {
			reader.close();
		}
	}

	public static URL doResolve(String fileName, String defaultFileName) {
		Assertion.notNull(fileName);
		// ---------------------------------------------------------------------
		final File file = new File(fileName);
		if (file.exists()) {
			try {
				return file.toURL();
			} catch (MalformedURLException e) {
				throw new KRuntimeException(e);
			}
		}

		final URL url = KraftBoot.class.getResource(defaultFileName);
		if (url != null) {
			return url;
		}
		throw new KRuntimeException("Le fichier " + defaultFileName + " est introuvable");
	}
}
