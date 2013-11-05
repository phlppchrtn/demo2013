package socket.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public final class TcpClient implements AutoCloseable {
	//	private final int port;
	private Socket socket;
	private BufferedReader in;

	TcpClient(int port) {
		//		this.port = port;
		//Ouverture de socket
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
		if (socket != null) {
			try {
				in.close();
				socket.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
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
