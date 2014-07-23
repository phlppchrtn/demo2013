package socket.tcp.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import socket.tcp.io.resp.VCommandHandler;
import socket.tcp.protocol.VCommand;

/**
 * A server using non blocking TCP socket .
 * @author pchretien
 *
 */
public final class TcpServer2 implements Runnable {
	private final int port;
	private final VCommandHandler commandHandler;
	private final ByteBuffer buffer;

	public TcpServer2(final int port, VCommandHandler commandHandler) {
		this.port = port;
		this.commandHandler = commandHandler;
		buffer = ByteBuffer.allocate(8192);
	}

	public void run() {
		try (Selector selector = Selector.open()) {
			try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
				serverSocketChannel.socket().bind(new InetSocketAddress(port));
				serverSocketChannel.configureBlocking(false);

				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
				// Wait for events
				while (true) {
					// Wait for an event
					/*int keys =*/selector.select();
					//System.out.println("select : " + keys);
					final Iterator<SelectionKey> selectionKeyIt = selector.selectedKeys().iterator();

					while (selectionKeyIt.hasNext()) {
						final SelectionKey selectionKey = selectionKeyIt.next();
						selectionKeyIt.remove();
						//---
						if (!selectionKey.isValid()) {
							continue;
						}
						//---
						if (selectionKey.isAcceptable()) {
							//On accepte une ouverture de socket 
							accept(selectionKey);
						}
						if (selectionKey.isReadable()) {
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

	private static void accept(SelectionKey selectionKey) throws IOException {
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();

		SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);

		socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
	}

	public void read(SelectionKey selectionKey) throws IOException {
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		read(socketChannel);
	}

	public void read(SocketChannel socketChannel) throws IOException {
		buffer.clear();
		int bytesRead = socketChannel.read(buffer);

		if (bytesRead == -1) {
			// Remote entity shut the socket down cleanly.
			socketChannel.close();
			//key.cancel();
			return;
		}
		if (bytesRead == 0) {
			System.out.println("readBytes : " + bytesRead);
			return;
		}

		buffer.flip();
		VCommand command = RedisProtocol2.decode(buffer);

		// We can use an optimized protocol 
		//		StringBuilder sb = new StringBuilder();
		//		while (buffer.hasRemaining()) {
		//			sb.append((char) buffer.get());
		//		}
		//		String command = sb.toString();

		//-------------------------------------------------
		//-------------------------------------------------
		String response = commandHandler.onCommand(command);
		buffer.clear();
		buffer.put(response.getBytes(RedisProtocol2.CHARSET));

		buffer.flip();
		while (buffer.hasRemaining()) {
			socketChannel.write(buffer);
		}
	}
}
