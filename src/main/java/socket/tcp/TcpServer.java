package socket.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public final class TcpServer implements Runnable {
	private final int port;

	TcpServer(int port) {
		this.port = port;
	}

	public void run() {
		try {
			doRun();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void doRun() throws IOException {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while (true) {
				Socket socket = serverSocket.accept();
				new Thread(new TcpSocket(socket)).start();
			}
		}
	}

	static String onQuery(Command command) {
		if ("ping".equals(command.getName())) {
			return ":555";
		} else if ("pong".equals(command.getName())) {
			return ":444";
		}
		return "aaaaaa";
	}

	//	}

	private static class TcpSocket implements Runnable {
		private final Socket socket;

		TcpSocket(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
				try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
					for (Command command = RedisProtocol.decode(in); command != null; command = RedisProtocol.decode(in)) {
						//						//						command	
						//						String inputLine, outputLine;
						//						//						System.out.println("inputing");
						//						while ((inputLine = in.readLine()) != null) {
						//							System.out.println("input =" + inputLine);

						String outputLine = onQuery(command);
						out.println(outputLine);
					}
					//					if (outputLine.toUpperCase().startsWith("BYE"))
					//						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
