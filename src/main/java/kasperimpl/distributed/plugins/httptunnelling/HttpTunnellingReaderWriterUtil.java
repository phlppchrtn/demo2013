package kasperimpl.distributed.plugins.httptunnelling;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Utilitaire centralisant la communication sur http au lieu de rmi .
 *
 * @author npiedeloup
 */
final class HttpTunnellingReaderWriterUtil {
	private static final String NULL_MAGIC_WORD = "null";

	private HttpTunnellingReaderWriterUtil() {
		//Helper : pas de constructeur
	}

	/**
	 * Ecrit dans le flux de sortie les paramètres de l'appel.
	 * @param data java.lang.Object
	 * @param output OutputStream
	 * @throws java.io.IOException   Exception de communication
	 */
	static void write(final Object data, final OutputStream output) throws IOException {
		final OutputStream gzipOutput = new GZIPOutputStream(new BufferedOutputStream(output));
		final ObjectOutputStream out = new ObjectOutputStream(gzipOutput);
		try {
			if (data == null) {
				out.writeObject(NULL_MAGIC_WORD);
			} else {
				out.writeObject(data);
			}
			out.flush();
		} finally {
			out.close();
		}
	}

	/**
	 * Lit l'objet renvoyé dans le flux de réponse.
	 * @return java.lang.Object
	 * @param input InputStream
	 * @throws java.io.IOException   Exception de communication
	 * @throws java.lang.ClassNotFoundException   Une classe transmise par le serveur n'a pas été trouvée
	 */
	static Object read(final InputStream input) throws IOException, ClassNotFoundException {
		final InputStream inputStream = new GZIPInputStream(new BufferedInputStream(input));
		final ObjectInputStream in = new ObjectInputStream(inputStream);
		try {
			Object result = in.readObject();
			if (NULL_MAGIC_WORD.equals(result)) {
				result = null;
			}
			return result;
		} finally {
			in.close();
		}
	}
}
