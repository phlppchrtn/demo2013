package socket.tcp;

import java.io.IOException;

public class RedisTest {
	//private static final String HOST = "kasper-redis";
	private static final String HOST = "localhost";
	//private static final int PORT = 6379;
	private static final int PORT = 4444;

	public static void main(String[] args) throws IOException, InterruptedException {
		new Thread(new TcpServer2(PORT)).start();
		Thread.sleep(10);

		//-----
		test(1, 500000);
		test(5, 100000);
		test(10, 50000);
		//test(20, 10000);
		test(25, 200000);
	}

	private static void test(int threadCount, int count) throws IOException, InterruptedException {
		long start = System.currentTimeMillis();
		//1 #  
		flushDb();
		//2 #
		if (threadCount > 1) {
			multiThread(threadCount, count);
		} else {
			monoThread(count);
		}
		//-----
		//3 #
		System.out.println("--------------------------------------------------- ");
		System.out.println("----- threads       : " + threadCount);
		System.out.println("----- count/thread  : " + count);
		System.out.println("----- elapsed time  : " + ((System.currentTimeMillis() - start) / 1000) + "s");
		System.out.println("----- check         : " + check(threadCount * count));
		System.out.println("--------------------------------------------------- ");
	}

	private static boolean check(int total) throws IOException {
		try (TcpClient2 tcpClient = new TcpClient2(HOST, PORT)) {
			long res = tcpClient.exec(new Command("llen", "test"));
			return total == res;
		}
	}

	private static void flushDb() throws IOException {
		try (TcpClient2 tcpClient = new TcpClient2(HOST, PORT)) {
			long res = tcpClient.exec(new Command("flushdb"));
			//System.out.println(">>Text: flushDB :" + res);
		}
	}

	private static void monoThread(int count) throws IOException {
		loop("mono", count);
	}

	private static void loop(String id, int count) {
		try (TcpClient2 tcpClient = new TcpClient2(HOST, PORT)) {
			for (int i = 0; i < count; i++) {
				long res = tcpClient.exec(new Command("lpush", "test", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaanode[" + id + "] :" + i));
				//				if ((i + 1) % 50000 == 0) {
				//					System.out.println(">>>lpush node [" + id + "] : >>" + res);
				//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void multiThread(int threadCount, int count) throws IOException, InterruptedException {
		Thread[] threads = new Thread[threadCount];
		for (int j = 0; j < threadCount; j++) {
			threads[j] = new Thread(new Sender("" + j, count));
			threads[j].start();
		}
		//		//Thread.sleep(50000);	
		for (int j = 0; j < threadCount; j++) {
			threads[j].join();
		}

	}

	//=====================================================
	//=====================================================
	//=====================================================
	public static class Sender implements Runnable {
		private final String id;
		private final int count;

		Sender(String id, int count) {
			this.id = id;
			this.count = count;
		}

		@Override
		public void run() {
			loop(id, count);
		}
	}
}
