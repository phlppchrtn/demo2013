package socket.tcp.io;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import socket.tcp.protocol.ReqResp;
import socket.tcp.protocol.VCommand;

public final class RespClient implements ReqResp {
	private final Socket socket;
	private final BufferedReader in;
	private final BufferedOutputStream out;

	public RespClient(String host, int port) {
		try {
			socket = new Socket(host, port);

			//			socket.setReuseAddress(true);
			//			socket.setKeepAlive(true); //Will monitor the TCP connection is valid
			//			socket.setTcpNoDelay(true); //Socket buffer Whetherclosed, to ensure timely delivery of data
			//			socket.setSoLinger(true, 0); //Control calls close () method, the underlying socket is closed immediately

			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		try {
			try {
				//imbriquer les try
				out.close();
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
		return 1; //execLong(command);
	}

	public long execLong(String command, String... args) throws IOException {
		push(new VCommand(command, args));
		return pullLong();
	}

	public String execString(String command, String... args) throws IOException {
		push(new VCommand(command, args));
		return pullString();
	}

	public String execBulk(String command, String... args) throws IOException {
		push(new VCommand(command, args));
		return pullBulk();
	}

	private void push(VCommand command) throws IOException {
		//System.out.println("exec command :" + command.getName());
		RespProtocol.encode(command, out);
		out.flush();
	}

	private long pullLong() throws IOException {
		//System.out.println("flush command :" + command.getName());
		//----
		String response = in.readLine();
		System.out.println("ask:response=" + response);
		if (!response.startsWith(":")) {
			throw new IllegalArgumentException(response);
		}
		return Long.valueOf(response.substring(1));
	}

	private String pullString() throws IOException {
		//System.out.println("flush command :" + command.getName());
		//----
		String response = in.readLine();
		System.out.println("ask:response=" + response);
		if (!response.startsWith("+")) {
			throw new IllegalArgumentException(response);
		}
		return response.substring(1);
	}

	public String pullBulk() throws IOException {
		String response = in.readLine();
		System.out.println("pullBulk: " + response);
		if ("*-1".equals(response)){
			return null;
		}
		if (!response.startsWith("$")) {
			throw new IllegalArgumentException(response);
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
