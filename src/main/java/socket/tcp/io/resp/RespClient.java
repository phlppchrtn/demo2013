package socket.tcp.io.resp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

public final class RespClient implements AutoCloseable {
	private final Socket socket;
	private final BufferedReader in;
	private final BufferedOutputStream out;

	public RespClient(String host, int port) {
		try {
			//			System.out.println(">>");
			//			SocketAddress socketAddress = new InetSocketAddress("172.20.0.9", 3128);
			//			Proxy proxy = new Proxy(Proxy.Type.SOCKS, socketAddress);
			//			System.out.println(">> + proxy");
			//			socket = new Socket(proxy);
			//			//socket = new Socket(Proxy.NO_PROXY);
			//
			//			InetSocketAddress dest = new InetSocketAddress(host, port);
			//			System.out.println(">> + connecting...");
			//			socket.connect(dest, 10000);
			//			System.out.println(">> + connected");

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

	public void close() throws IOException {
		try {
			try {
				out.close();
			} finally {
				in.close();
			}
		} finally {
			//On ferme tjrs la socket
			socket.close();
		}
	}

	public List<String> execArray(String command, String... args) throws IOException {
		RespProtocol.push(out, command, args);
		return (List<String>) RespProtocol.pull(in, '*');
	}

	public long execLong(String command, String... args) throws IOException {
		RespProtocol.push(out, command, args);
		return (Long) RespProtocol.pull(in, ':');
	}

	public String execString(String command, String... args) throws IOException {
		RespProtocol.push(out, command, args);
		return (String) RespProtocol.pull(in, '+');
	}

	public String execBulk(String command, String... args) throws IOException {
		RespProtocol.push(out, command, args);
		return (String) RespProtocol.pull(in, '$');
	}
}
