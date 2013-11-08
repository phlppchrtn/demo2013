package socket.tcp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public final class TcpClient implements AutoCloseable {
	private final Socket socket;
	private final BufferedReader in;
	private final BufferedOutputStream buffer;

	TcpClient(String host, int port) {
		try {
			socket = new Socket(host, port);

			//			socket.setReuseAddress(true);
			//			socket.setKeepAlive(true); //Will monitor the TCP connection is valid
			//			socket.setTcpNoDelay(true); //Socket buffer Whetherclosed, to ensure timely delivery of data
			//			socket.setSoLinger(true, 0); //Control calls close () method, the underlying socket is closed immediately

			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffer = new BufferedOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		try {
			try {
				//imbriquer les try
				buffer.close();
				in.close();
			} finally {
				//On ferme tjrs la socket
				socket.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public long exec(Command command) throws IOException {
		push(command);
		return pull();
	}

	private void push(Command command) throws IOException {
		//System.out.println("exec command :" + command.getName());
		RedisProtocol.encode(command, buffer);
		buffer.flush();
	}

	private long pull() throws IOException {
		//System.out.println("flush command :" + command.getName());
		//----
		String response = in.readLine();
		//	System.out.println("ask:response=" + response);
		return Long.valueOf(response.substring(1));
	}
}
