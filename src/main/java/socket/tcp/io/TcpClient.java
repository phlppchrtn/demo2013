package socket.tcp.io;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import socket.tcp.protocol.ReqResp;
import socket.tcp.protocol.VCommand;

public final class TcpClient implements ReqResp, AutoCloseable {
	private final Socket socket;
	private final BufferedReader in;
	private final BufferedOutputStream buffer;

	public TcpClient(String host, int port) {
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

	public long exec(VCommand command) throws IOException {
		return execLong(command);
	}

	public long execLong(VCommand command) throws IOException {
		push(command);
		return pullLong();
	}

	public String execString(VCommand command) throws IOException {
		push(command);
		return pullString();
	}

	public String execBulk(VCommand command) throws IOException {
		push(command);
		return pullBulk();
	}

	private void push(VCommand command) throws IOException {
		//System.out.println("exec command :" + command.getName());
		RedisProtocol.encode(command, buffer);
		buffer.flush();
	}

	private long pullLong() throws IOException {
		//System.out.println("flush command :" + command.getName());
		//----
		String response = in.readLine();
		System.out.println("ask:response=" + response);
		if (!response.startsWith(":")) {
			throw new IllegalArgumentException();
		}
		return Long.valueOf(response.substring(1));
	}

	private String pullString() throws IOException {
		//System.out.println("flush command :" + command.getName());
		//----
		String response = in.readLine();
		System.out.println("ask:response=" + response);
		if (!response.startsWith("+")) {
			throw new IllegalArgumentException();
		}
		return response.substring(1);
	}

	public String pullBulk() throws IOException {
		//		System.out.println("sub: " + line.substring(1));
		String response = in.readLine();
		if (!response.startsWith("$")) {
			throw new IllegalArgumentException();
		}
		int n = Integer.valueOf(response.substring(1));
		if (n < 0) {
			return null;
		} else if (n == 0) {
			return "";
		}
		//	System.out.println("bulk 0>" + response);
		response = in.readLine();
		//	System.out.println("bulk 1>" + response);
		return response;
		//				
		//		String[] args = new String[n];
		//		for (int i = 0; i < n; i++) {
		//			response = in.readLine();
		//			//	System.out.println("line('" + i + "') : " + line);
		//			if (!line.startsWith("$")) {
		//				throw new RuntimeException("protocol must contains lines with $");
		//			}
		//			//n = Integer.valueOf(line.substring(1)); 
		//			//On n'exploite pas cette info
		//			line = input.readLine();
		//			if (i == 0) {
		//				commandName = line;
		//				//	System.out.println("name  : " + line);
		//			} else {
		//				args[i - 1] = line;
		//			}
		//		}
	}
}
