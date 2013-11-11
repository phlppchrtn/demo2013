package socket.classloader.client;

import java.io.IOException;

public final class TcpClassLoader extends ClassLoader {
	private final TcpCLClient tcpCLClient;

	public TcpClassLoader(String host, int port) {
		tcpCLClient = new TcpCLClient(host, port);
	}

	public Class findClass(String name) {
		try {
			byte[] b = tcpCLClient.exec(name);
			System.out.println("ClassName  : " + name);
			System.out.println("bytes.size : " + b.length);
			return defineClass(name, b, 0, b.length);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
