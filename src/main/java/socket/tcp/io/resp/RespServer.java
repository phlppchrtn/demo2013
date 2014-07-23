package socket.tcp.io.resp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public final class RespServer implements Runnable {
	private final int port;
	private final RespCommandHandler respCommandHandler;

	public RespServer(int port, RespCommandHandler respCommandHandler) {
		this.port = port;
		this.respCommandHandler = respCommandHandler;
	}

	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while (true) {
				Socket socket = serverSocket.accept();
				new Thread(new TcpSocket(respCommandHandler, socket)).start();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static class TcpSocket implements Runnable {
		private final Socket socket;
		private final RespCommandHandler respCommandHandler;

		TcpSocket(RespCommandHandler respCommandHandler, Socket socket) {
			this.socket = socket;
			this.respCommandHandler = respCommandHandler;
		}

		@Override
		public void run() {
			//			try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
				for (RespCommand command = RespProtocol.readCommand(in); command != null; command = RespProtocol.readCommand(in)) {
					respCommandHandler.onCommand(socket.getOutputStream(), command);
				}
			}
			//			} catch (IOException e) {
			//				throw new RuntimeException(e);
			//			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
