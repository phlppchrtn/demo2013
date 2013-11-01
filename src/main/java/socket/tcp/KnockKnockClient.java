package socket.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class KnockKnockClient {
	public static void main(String[] args) throws IOException {
		//Ouverture de socket
		try (Socket socket = new Socket("localhost", 4444)) {
			//
			try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

				try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

					try (BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
						String fromServer;
						String fromUser;

						while ((fromServer = in.readLine()) != null) {
							System.out.println("Server: " + fromServer);
							if (fromServer.toUpperCase().startsWith("BYE")) {
								break;
							}
							fromUser = stdIn.readLine();
							if (fromUser != null) {
								System.out.println("Client: " + fromUser);
								out.println(fromUser);
							}
						}

					}
				}
			}
		}
	}
}
