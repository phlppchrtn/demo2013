package socket.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public final class TcpServer2 implements Runnable {
	private final String host;
	private final int port;
	private final ByteBuffer buffer;
	private long datas;

	TcpServer2(final String host, final int port) {
		this.host = host;
		this.port = port;
		buffer = ByteBuffer.allocate(8192);
	}

	public void run() {
		try (Selector selector = Selector.open()) {
			//		new Thread(new Listener(selector)).start();

			try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
				serverSocketChannel.socket().bind(new InetSocketAddress(host, port));
				serverSocketChannel.configureBlocking(false);

				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // | SelectionKey.OP_READ | SelectionKey.OP_CONNECT | SelectionKey.OP_WRITE);
				// Wait for events
				while (true) {
					// Wait for an event
					/*int keys =*/selector.select();
					//	System.out.println("select");
					final Iterator<SelectionKey> selectionKeyIt = selector.selectedKeys().iterator();

					while (selectionKeyIt.hasNext()) {
						final SelectionKey selectionKey = selectionKeyIt.next();
						selectionKeyIt.remove();

						if (!selectionKey.isValid()) {
							continue;
						}
						//---
						if (selectionKey.isAcceptable()) {
							//On accepte une ouverture de socket 
							accept(selectionKey);
						} else if (selectionKey.isReadable()) {
							//On lit sur une socket
							read(selectionKey);
						}
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void accept(SelectionKey selectionKey) throws IOException {
		System.out.println("accept  >>>>" + selectionKey);
		// For an accept to be pending the channel must be a server socket channel.
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();

		// Accept the connection and make it non-blocking
		SocketChannel socketChannel = serverSocketChannel.accept();
		Socket socket = socketChannel.socket();
		socketChannel.configureBlocking(false);

		System.out.println("register>>>>" + socketChannel);

		// Register the new SocketChannel with our Selector, indicating
		// we'd like to be notified when there's data waiting to be read
		socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
	}

	public void read(SelectionKey selectionKey) throws IOException {
		//System.out.println("reading...");
		//On recoit une info du client.
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		buffer.clear();
		int bytesRead = socketChannel.read(buffer);
		System.out.println("readBytes : " + bytesRead);
		//System.out.println("readSck : " + socketChannel);
		//		while (bytesRead != -1) {
		buffer.flip();

		StringBuilder sb = new StringBuilder();
		while (buffer.hasRemaining()) {
			sb.append((char) buffer.get());
		}

		String command = sb.toString();
		if (command.contains("flush")) {
			datas = 0;
		} else if (command.contains("llen")) {
			String count = ":" + datas;
			buffer.put(count.getBytes(RedisProtocol.CHARSET));
		} else if (command.contains("lpush")) {
			datas++;
		}
	}
} //	}
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
