package socket.tcp.io.resp;

import io.vertigo.kernel.lang.Assertion;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import socket.tcp.protocol.VCommand;

public final class RespClient implements AutoCloseable {
	private final Socket socket;
	private final BufferedReader in;
	private final BufferedOutputStream out;

	public RespClient(String host, int port) {
		try {
			socket = new Socket(host, port);
			// socket.setReuseAddress(true);
			// socket.setKeepAlive(true); //Will monitor the TCP connection is valid
			// socket.setTcpNoDelay(true); //Socket buffer Whetherclosed, to ensure timely delivery of data
			// socket.setSoLinger(true, 0); //Control calls close () method, the underlying socket is closed immediately

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

	public List<String> execArray(String command, String... args) throws IOException {
		push(new VCommand(command, args));
		return (List<String>) pull('*');
	}

	public long execLong(String command, String... args) throws IOException {
		push(new VCommand(command, args));
		return (Long) pull(':');
	}

	public String execString(String command, String... args) throws IOException {
		push(new VCommand(command, args));
		return (String) pull('+');
	}

	public String execBulk(String command, String... args) throws IOException {
		push(new VCommand(command, args));
		return (String) pull('$');
	}

	private void push(VCommand command) throws IOException {
		//System.out.println("exec command :" + command.getName());
		RespProtocol.encode(command, out);
		out.flush();
	}

	private Object pull(char expected) throws IOException {
		String response = in.readLine();
		System.out.println(expected + ":" + response);
		//---
		if ("*-1".equals(response)) {
			return null;
		}
		//----
		Assertion.checkArgument('?' == expected || expected == response.charAt(0), "exepected {0}, find {1}", expected, response.charAt(0));
		//----
		switch (response.charAt(0)) {
			case ':': //number
				return Long.valueOf(response.substring(1));
			case '+': //string
				return response.substring(1);
			case '$': //bulk 
				int n = Integer.valueOf(response.substring(1));
				if (n < 0) {
					return null;
				} else if (n == 0) {
					return "";
				}
				return in.readLine();
			case '*': //array
				int m = Integer.valueOf(response.substring(1));
				List list = new ArrayList<>();
				for (int i = 0; i < m; i++) {
					list.add(pull('?'));
				}
				return list;
			default:
				throw new IllegalArgumentException(response);
		}

	}
	//
	//	private long pullLong() throws IOException {
	//		//System.out.println("flush command :" + command.getName());
	//		//----
	//		String response = in.readLine();
	//		//System.out.println("ask:response=" + response);
	//		if (!response.startsWith(":")) {
	//			throw new IllegalArgumentException(response);
	//		}
	//		return Long.valueOf(response.substring(1));
	//	}
	//
	//	private String pullString() throws IOException {
	//		//System.out.println("flush command :" + command.getName());
	//		//----
	//		String response = in.readLine();
	//		//System.out.println("ask:response=" + response);
	//		if (!response.startsWith("+")) {
	//			throw new IllegalArgumentException(response);
	//		}
	//		return response.substring(1);
	//	}
	//
	//	public String pullArray() throws IOException {
	//		String response = in.readLine();
	//		//System.out.println("pullBulk: " + response);
	//		if ("*-1".equals(response)) {
	//			return null;
	//		}
	//		if (!response.startsWith("*")) {
	//			throw new IllegalArgumentException(response);
	//		}
	//		int n = Integer.valueOf(response.substring(1));
	//		if (n < 0) {
	//			return null;
	//		} else if (n == 0) {
	//			return "";
	//		}
	//		for (int i = 0; i < n; i++) {
	//			response = in.readLine();
	//
	//		}
	//		//	System.out.println("bulk 0>" + response);
	//		response = in.readLine();
	//		//	System.out.println("bulk 1>" + response);
	//		return response;
	//	}
	//
	//	public String pullBulk() throws IOException {
	//		String response = in.readLine();
	//		//System.out.println("pullBulk: " + response);
	//		if ("*-1".equals(response)) {
	//			return null;
	//		}
	//		if (!response.startsWith("$")) {
	//			throw new IllegalArgumentException(response);
	//		}
	//		int n = Integer.valueOf(response.substring(1));
	//		if (n < 0) {
	//			return null;
	//		} else if (n == 0) {
	//			return "";
	//		}
	//		response = in.readLine();
	//		return response;
	//	}

}
