package socket.classloader;

import io.vertigo.kernel.util.ClassUtil;

public class Test {
	private static final String HOST = "localhost";
	//private static final int PORT = 6379;
	private static final int PORT = 4444;

	public static void main(String[] args) {
		new Thread(new TcpCLServer(PORT)).start();
		//----
		Class clazz = new TcpClassLoader(HOST, PORT).findClass(CalculatorImpl.class.getName());
		Calculator calculator = (Calculator) ClassUtil.newInstance(clazz);
		System.out.println(">>>3+2=" + calculator.add(3, 2));
	}
}
