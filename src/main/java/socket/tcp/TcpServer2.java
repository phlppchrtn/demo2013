package socket.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * A server using non blocking TCP socket .
 * @author pchretien
 *
 */
public final class TcpServer2 implements Runnable {
	private final int port;
	private final ByteBuffer buffer;
	//---
	private long datas;

	TcpServer2(final int port) {
		this.port = port;
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

	private void accept(SelectionKey selectionKey) throws IOException {
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
		Command command = RedisProtocol2.decode(buffer);

		// We can use an optimized protocol 
		//		StringBuilder sb = new StringBuilder();
		//		while (buffer.hasRemaining()) {
		//			sb.append((char) buffer.get());
		//		}
		//		String command = sb.toString();

		//-------------------------------------------------
		//-------------------------------------------------
		String response = onCommand(command);
		buffer.clear();
		buffer.put(response.getBytes(RedisProtocol.CHARSET));

		buffer.flip();
		while (buffer.hasRemaining()) {
			socketChannel.write(buffer);
		}
	}

	public String onCommand(Command command) {
		//System.out.println("command (" + command.length() + "): " + command);
		if ("flushdb".equals(command.getName())) {
			datas = 0;
		} else if ("llen".equals(command.getName())) {
		} else if ("lpush".equals(command.getName())) {
			datas++;
		} else {
			throw new RuntimeException("Command inconnue");
		}

		return ":" + datas;
	}
}
