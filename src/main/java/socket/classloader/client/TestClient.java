package socket.classloader.client;

public class TestClient {
	private static final String HOST = "pchretien";
	//private static final int PORT = 6379;
	private static final int PORT = 4444;

	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		int count;
		if (args == null || args.length == 0) {
			count = 5;
		} else {
			count = Integer.valueOf(args[0]);
		}
		System.out.println("======================");
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			test();
		}
		System.out.println("======================");
		System.out.println("count        : " + count);
		System.out.println("elasped time : " + (System.currentTimeMillis() - start) + " ms");
		System.out.println("======================");
	}

	private static void test() throws InstantiationException, IllegalAccessException {
		Class clazz = new TcpClassLoader(HOST, PORT).findClass("socket.classloader.CalculatorImpl");
		Calculator calculator = (Calculator) clazz.newInstance();
		System.out.println(">>>3+2=" + calculator.f(3, 2));
		System.out.println(">>>7+9=" + calculator.f(7, 9));
	}
}
