package socket.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.sshd.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

import socket.tcp.KnockKnockProtocol;

public class KnockKnockServer {
	public static void main(String[] args) throws IOException {

		SshServer sshd = SshServer.setUpDefaultServer();
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
		//		sshd.setShellFactory(new ProcessShellFactory(new String[] { "/bin/sh", "-i", "-l" }));
		//sshd.setCommandFactory(new ScpCommandFactory());

		sshd.start();

		try (ServerSocket serverSocket = new ServerSocket(4444)) {
			try (Socket clientSocket = serverSocket.accept()) {
				try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
					try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
						String inputLine, outputLine;
						KnockKnockProtocol kkp = new KnockKnockProtocol();

						outputLine = kkp.processInput(null);
						out.println(outputLine);

						while ((inputLine = in.readLine()) != null) {
							outputLine = kkp.processInput(inputLine);
							out.println(outputLine);
							if (outputLine.equals("Bye."))
								break;
						}
					}
				}
			}
		}
	}
}
