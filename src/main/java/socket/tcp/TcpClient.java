package socket.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public final class TcpClient implements AutoCloseable {
	private final Socket socket;
	private final BufferedReader in;

	TcpClient(int port) {
		try {
			socket = new Socket("localhost", port);

			//			socket.setReuseAddress(true);
			//			socket.setKeepAlive(true); //Will monitor the TCP connection is valid
			//			socket.setTcpNoDelay(true); //Socket buffer Whetherclosed, to ensure timely delivery of data
			//			socket.setSoLinger(true, 0); //Control calls close () method, the underlying socket is closed immediately

			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		try {
			try {
				in.close();
			} finally {
				//On ferme tjrs la socket
				socket.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String exec(Command command) throws IOException {
		//System.out.println("exec command :" + command.getName());
		RedisProtocol.encode(command, socket.getOutputStream());
		socket.getOutputStream().flush();
		//System.out.println("flush command :" + command.getName());
		//----
		String response = in.readLine();
		System.out.println("ask:response=" + response);
		return response;
	}
}
