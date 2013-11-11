package socket.classloader.client;

public class TestClient {
	private static final String HOST = "pchretien-port";
	//private static final int PORT = 6379;
	private static final int PORT = 4444;

	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		System.out.println("======================");
		test();
		test();
		System.out.println("======================");
	}

	private static void test() throws InstantiationException, IllegalAccessException {
		Class clazz = new TcpClassLoader(HOST, PORT).findClass("socket.classloader.CalculatorImpl");
		Calculator calculator = (Calculator) clazz.newInstance();
		System.out.println(">>>3+2=" + calculator.add(3, 2));
		System.out.println(">>>7+9=" + calculator.add(7, 9));
	}
}
