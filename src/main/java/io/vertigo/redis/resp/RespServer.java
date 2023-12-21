package io.vertigo.redis.resp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public final class RespServer implements Runnable {
	private final int port;
	private final RespCommandHandler respCommandHandler;

	public RespServer(final int port, final RespCommandHandler respCommandHandler) {
		this.port = port;
		this.respCommandHandler = respCommandHandler;
	}

	@Override
	public void run() {
		System.out.println("start server " + port);
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while (true) {
				final Socket socket = serverSocket.accept();
				new Thread(new TcpSocket(respCommandHandler, socket)).start();
			}
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static class TcpSocket implements Runnable {
		private final Socket socket;
		private final RespCommandHandler respCommandHandler;

		TcpSocket(final RespCommandHandler respCommandHandler, final Socket socket) {
			this.socket = socket;
			this.respCommandHandler = respCommandHandler;
		}

		@Override
		public void run() {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
				for (RespCommand command = RespProtocol.readCommand(in); command != null; command = RespProtocol.readCommand(in)) {
					respCommandHandler.onCommand(new RespWriter(socket.getOutputStream()), command);
				}
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
