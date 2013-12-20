package kraft;

import java.io.IOException;
import java.net.URI;
import java.util.Map.Entry;

import javax.ws.rs.core.UriBuilder;

import kraft.webservices.Datas;
import kraft.webservices.Managers;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

final class KraftServer {
	private final KasperBridge kasperServer = new KasperBridge();
	private HttpServer httpServer;
	private final Config config;

	KraftServer(Config config) {
		this.config = config;
	}

	void start() throws Exception {
		kasperServer.start();
		httpServer = createServer(config);
	}

	void stop() throws Exception {
		httpServer.stop();
		kasperServer.stop();
	}

	private static HttpServer createServer(Config config) throws IOException {
		System.out.println("Starting grizzly...");
		final ResourceConfig rc = new ClassNamesResourceConfig(kraft.webservices.Definitions.class, Datas.class, Managers.class);

		URI baseURI = UriBuilder.fromUri("http://localhost/").port(80).build();

		System.out.println(String.format("Jersey app started with WADL available at " + "%sapplication.wadl\nenter to stop it...", baseURI, baseURI));

		HttpServer httpServer = GrizzlyServerFactory.createHttpServer(baseURI, rc);

		for (Entry<String, String> entry : config.staticHandlers.entrySet()) {
			System.out.println("____with config : " + entry.getKey() + "==>" + entry.getValue());
			httpServer.getServerConfiguration().addHttpHandler(new StaticHttpHandler(entry.getKey()),
			// new StaticNoCacheHttpHandler(entry.getKey()),
					entry.getValue());
		}
		return httpServer;
	}

	public void reset() throws Exception {
		stop();
		start();
	}
}
