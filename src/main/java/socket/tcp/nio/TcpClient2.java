package socket.tcp.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import socket.tcp.protocol.ReqResp;
import socket.tcp.protocol.VCommand;

public final class TcpClient2 implements ReqResp {
	private SocketChannel socketChannel;
	private ByteBuffer buffer;

	public TcpClient2(final String host, final int port) {
		try {
			socketChannel = SocketChannel.open(new InetSocketAddress(host, port));

			buffer = ByteBuffer.allocate(1024);
			//			buffer.clear();
			//			buffer.put(newData.getBytes());

			//			socket.setReuseAddress(true);
			//			socket.setKeepAlive(true); //Will monitor the TCP connection is valid
			//			socket.setTcpNoDelay(true); //Socket buffer Whetherclosed, to ensure timely delivery of data
			//			socket.setSoLinger(true, 0); //Control calls close () method, the underlying socket is closed immediately

			//			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			//On ferme tjrs la socket
			socketChannel.close();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	//Command ==> 
	@Override
	public long exec(final VCommand command) throws IOException {
		push(command);
		return pull();
	}

	private void push(final VCommand command) throws IOException {
		//	System.out.println("exec command :" + command.getName());
		buffer.clear();
		RedisProtocol2.encode(command, buffer);

		buffer.flip();
		//Et on �crit les donn�es
		while (buffer.hasRemaining()) {
			socketChannel.write(buffer);
		}
	}

	private long pull() throws IOException {
		buffer.clear();
		/*int bytesRead =*/socketChannel.read(buffer);
		//		while (bytesRead != -1) {
		//System.out.println("Read " + bytesRead);
		buffer.flip();

		boolean first = true;
		final StringBuilder sb = new StringBuilder();
		while (buffer.hasRemaining()) {
			if (first) {
				//:98
				//	char firstChar = (char) buffer.get();
				first = false;
			} else {
				sb.append((char) buffer.get());
			}
		}
		if (sb.toString().startsWith("OK")) {
			return 200;
		}
		return Long.valueOf(sb.toString().trim());
	}
}
