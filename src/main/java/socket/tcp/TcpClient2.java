package socket.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public final class TcpClient2 implements AutoCloseable {
	//	private final int port;
	private SocketChannel socketChannel;
	private ByteBuffer buffer;

	public TcpClient2(int port) {
		//		this.port = port;
		//Ouverture de socket
		try {
			socketChannel = SocketChannel.open();
			socketChannel.connect(new InetSocketAddress("localhost", port));

			buffer = ByteBuffer.allocate(1024);
			//			buffer.clear();
			//			buffer.put(newData.getBytes());

			//			socket.setReuseAddress(true);
			//			socket.setKeepAlive(true); //Will monitor the TCP connection is valid
			//			socket.setTcpNoDelay(true); //Socket buffer Whetherclosed, to ensure timely delivery of data
			//			socket.setSoLinger(true, 0); //Control calls close () method, the underlying socket is closed immediately

			//			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		try {
			//On ferme tjrs la socket
			socketChannel.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void exec(Command command) throws IOException {
		//System.out.println("exec command :" + command.getName());
		buffer.clear();
		RedisProtocol2.encode(command, buffer);

		buffer.flip();
		//Et on écrit les données
		while (buffer.hasRemaining()) {
			socketChannel.write(buffer);
		}
	}

	public long reply() throws IOException {
		buffer.clear();
		int bytesRead = socketChannel.read(buffer);
		//		while (bytesRead != -1) {
		System.out.println("Read " + bytesRead);
		buffer.flip();

		boolean first = true;
		char firstChar;
		StringBuilder sb = new StringBuilder();
		while (buffer.hasRemaining()) {
			if (first) {
				//:98
				firstChar = (char) buffer.get();
				first = false;
			} else {
				sb.append((char) buffer.get());
			}
		}
		return Long.valueOf(sb.toString().trim());
	}
}
