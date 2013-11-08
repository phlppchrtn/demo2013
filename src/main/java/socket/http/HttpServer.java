package socket.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple server HTTP with only a header and a basic htlm content. 
 * To test you need 
 *  - to execute this class as a java application 
 *   -to open localhost:88 with your browser.
 *   
 * @author pchretien
 */
public class HttpServer {
	private static final int DEFAULT_PORT = 88;
	private final int port;

	public static void main(String[] args) throws Exception {
		new HttpServer(DEFAULT_PORT).start();
	}

	HttpServer(int port) {
		this.port = port;
	}

	void start() throws IOException {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			System.out.println("Http server on port : " + port);
			while (true) {
				// on reste bloqué sur l'attente d'une demande client
				try (Socket clientSocket = serverSocket.accept()) {
					System.out.println("A new client is connected");
					// on ouvre un flux de conversation

					try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
						try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
							// chaque fois qu'une donnée est lue sur le réseau on la renvoi sur
							// le flux d'écriture.
							// la donnée lue est donc retournée exactement au même client.
							String s;
							while ((s = in.readLine()) != null) {
								System.out.println(s);
								if (s.isEmpty()) {
									break;
								}
							}

							String content = new StringBuilder()//
									.append("<TITLE>Hello World</TITLE>") //
									.append("<P>This is a basic sample page.</P>")//
									.toString();

							out.write("HTTP/1.0 200 OK\r\n");
							out.write("Server: Vertigo");
							out.write("Content-Type: text/html\r\n");
							out.write("Content-Length: " + content.length() + "r\n");
							out.write("\r\n");
							out.write(content);

							// on ferme les flux.
							System.out.println("Connection with client is closed");
						}
					}
				}
			}
		}
	}
}
