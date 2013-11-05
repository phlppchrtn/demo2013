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

	public String exec(Command command) throws IOException {
		//System.out.println("exec command :" + command.getName());
		buffer.clear();
		RedisProtocol2.encode(command, buffer);

		buffer.flip();
		//Et on écrit les données
		while (buffer.hasRemaining()) {
			socketChannel.write(buffer);
		}

		//System.out.println("flush command :" + command.getName());
		//----
		String response = null; //buffer.read();
		System.out.println("ask:response=" + response);
		return response;
	}
}
