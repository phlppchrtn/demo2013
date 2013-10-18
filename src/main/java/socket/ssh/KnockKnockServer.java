package socket.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.sshd.SshServer;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;

public class KnockKnockServer {
	/**
	 * Very basic PasswordAuthenticator used for unit tests.
	 */
	public static class MyPasswordAuthenticator implements PasswordAuthenticator {

		public boolean authenticate(String username, String password, ServerSession session) {
			boolean retour = false;

			if ("nico".equals(username) && "nico".equals(password)) {
				retour = true;
			}

			return retour;
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		SshServer sshServer = SshServer.setUpDefaultServer();
		sshServer.setPort(22);
		sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
		//		sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
		sshServer.setPasswordAuthenticator(new MyPasswordAuthenticator());

		sshServer.setShellFactory(new org.apache.sshd.common.Factory<Command>() {

			@Override
			public Command create() {
				System.out.println(">>>command");
				return new Command() {
					private InputStream in;

					private OutputStream out;
					private OutputStream errorOut;

					private ExitCallback exitCallback;

					@Override
					public void start(Environment environment) throws IOException {
						System.out.println(">>>start");
						//environment.
						new PrintWriter(out).write("Bienvenue dans un monde meilleur");
						out.flush();
					}

					@Override
					public void setOutputStream(OutputStream out) {
						this.out = out;
					}

					@Override
					public void setInputStream(InputStream in) {
						this.in = in;
					}

					@Override
					public void setExitCallback(ExitCallback exitCallback) {
						this.exitCallback = exitCallback;
					}

					@Override
					public void setErrorStream(OutputStream errorOut) {
						this.errorOut = errorOut;
					}

					@Override
					public void destroy() {
						//
					}
				};
			}
		});
		//		sshServer.setCommandFactory(new ScpCommandFactory(myCommandFactory));

		//		sshServer.setCommandFactory(new SCPCommandFactory(getContext()));
		//		sshServer.setKeyPairProvider(new URLKeyPairProvider(key));
		sshServer.start();
		Thread.sleep(100000);
		//		sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {

		//			@Override
		//			public boolean authenticate(String arg0, String arg1, ServerSession arg2) {
		//				return true;
		//			}
		//		});

		//		SshServer sshd = SshServer.setUpDefaultServer();
		//		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
		//		//		sshd.setShellFactory(new ProcessShellFactory(new String[] { "/bin/sh", "-i", "-l" }));
		//		//sshd.setCommandFactory(new ScpCommandFactory());
		//		sshServer.start();
		//
		//		try (ServerSocket serverSocket = new ServerSocket(4444)) {
		//			try (Socket clientSocket = serverSocket.accept()) {
		//				try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
		//					try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
		//						String inputLine, outputLine;
		//						KnockKnockProtocol kkp = new KnockKnockProtocol();
		//
		//						outputLine = kkp.processInput(null);
		//						out.println(outputLine);
		//
		//						while ((inputLine = in.readLine()) != null) {
		//							outputLine = kkp.processInput(inputLine);
		//							out.println(outputLine);
		//							if (outputLine.equals("Bye."))
		//								break;
		//						}
		//					}
		//				}
		//			}
		//		}
	}
}
