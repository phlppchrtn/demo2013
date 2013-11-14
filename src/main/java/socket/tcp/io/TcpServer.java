package socket.tcp.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import socket.tcp.protocol.VCommand;
import socket.tcp.protocol.VCommandHandler;

public final class TcpServer implements Runnable {
	private final int port;
	private final VCommandHandler commandHandler;

	public TcpServer(int port, VCommandHandler commandHandler) {
		this.port = port;
		this.commandHandler = commandHandler;
	}

	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while (true) {
				Socket socket = serverSocket.accept();
				new Thread(new TcpSocket(commandHandler, socket)).start();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static class TcpSocket implements Runnable {
		private final Socket socket;
		private final VCommandHandler commandHandler;

		TcpSocket(VCommandHandler commandHandler, Socket socket) {
			this.socket = socket;
			this.commandHandler = commandHandler;
		}

		@Override
		public void run() {
			try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
				try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
					for (VCommand command = RedisProtocol.decode(in); command != null; command = RedisProtocol.decode(in)) {
						String outputLine = commandHandler.onCommand(command);
						out.println(outputLine);
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}
}
