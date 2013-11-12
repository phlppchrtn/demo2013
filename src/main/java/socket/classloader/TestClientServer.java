package socket.classloader;

import socket.classloader.client.Calculator;
import socket.classloader.client.TcpClassLoader;
import socket.classloader.server.TcpCLServer;

public class TestClientServer {
	private static final String HOST = "localhost";
	//private static final int PORT = 6379;
	private static final int PORT = 4444;

	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		new Thread(new TcpCLServer(PORT)).start();
		//----
		Class clazz = new TcpClassLoader(HOST, PORT).findClass(CalculatorImpl.class.getName());
		Calculator calculator = (Calculator) clazz.newInstance();
		System.out.println(">>>3+2=" + calculator.f(3, 2));
	}
}
