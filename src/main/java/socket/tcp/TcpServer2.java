package socket.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public final class TcpServer2 implements Runnable {
	private final int port;

	TcpServer2(int port) {
		this.port = port;
	}

	public void run() {
		try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
			serverSocketChannel.configureBlocking(false);

			while (true) {
				SocketChannel socketChannel = serverSocketChannel.accept();

			}
		} catch (IOException e) {
			throw new RuntimeException(e);
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
	//		@Override
	//		public void run() {
	//			try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
	//				try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
	//					for (Command command = RedisProtocol.decode(in); command != null; command = RedisProtocol.decode(in)) {
	//						String outputLine = onQuery(command);
	//						out.println(outputLine);
	//					}
	//				}
	//			} catch (IOException e) {
	//				throw new RuntimeException(e);
	//			}
	//		}
	//
	//	}
}
