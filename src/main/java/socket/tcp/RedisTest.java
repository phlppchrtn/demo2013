package socket.tcp;

import java.io.IOException;

public class RedisTest {
	//private static final String HOST = "kasper-redis";
	private static final String HOST = "localhost";
	private static final int PORT = 6379;

	//	public static void main(String[] args) throws IOException, InterruptedException {
	//		multiThread(5, 100000);
	//	}

	public static void main(String[] args) throws IOException, InterruptedException {
		multiThread(5, 100000);
		multiThread(5, 100000);
		multiThread(50, 10000);
		multiThread(50, 10000);
		multiThread(10, 50000);
		multiThread(10, 50000);
		multiThread(100, 5000);
		multiThread(100, 5000);
		//monoThread(100000);
	}

	private static boolean check(int total) throws IOException {
		try (TcpClient2 tcpClient = new TcpClient2(HOST, PORT)) {
			tcpClient.exec(new Command("llen", "test"));
			long res = tcpClient.reply();
			return total == res;
		}
	}

	private static void flushDb() throws IOException {
		try (TcpClient2 tcpClient = new TcpClient2(HOST, PORT)) {
			tcpClient.exec(new Command("flushdb"));
		}
	}

	private static void monoThread(int count) throws IOException {
		try (TcpClient2 tcpClient = new TcpClient2(HOST, PORT)) {
			//tcpClient.exec(new Command("flushDb"));
			for (int i = 0; i < count; i++) {
				tcpClient.exec(new Command("lpush", "test", " :" + i));
				long res = tcpClient.reply();
				if (i % 1000 == 0) {
					System.out.println(">>>lpush  : " + i + " >>" + res);
				}
			}
		}
	}

	private static void multiThread(int threadCount, int count) throws IOException, InterruptedException {
		flushDb();

		long start = System.currentTimeMillis();

		Thread[] threads = new Thread[threadCount];
		for (int j = 0; j < threadCount; j++) {
			threads[j] = new Thread(new Sender("" + j, count));
			threads[j].start();
		}
		//		//Thread.sleep(50000);	
		for (int j = 0; j < threadCount; j++) {
			threads[j].join();
		}
		System.out.println("--------------------------------------------------- ");
		System.out.println("----- mode : multi threads");
		System.out.println("----- threads  : " + threadCount);
		System.out.println("----- count/thread  : " + count);
		System.out.println("----- elapsed time >>" + ((System.currentTimeMillis() - start) / 1000) + "s");
		System.out.println("----- check  : " + check(threadCount * count));
		System.out.println("--------------------------------------------------- ");
	}

	public static class Sender implements Runnable {
		private final String id;
		private final int count;

		Sender(String id, int count) {
			this.id = id;
			this.count = count;
		}

		@Override
		public void run() {
			try (TcpClient2 tcpClient = new TcpClient2(HOST, PORT)) {
				for (int i = 0; i < count; i++) {
					tcpClient.exec(new Command("lpush", "test", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaanode[" + id + "] :" + i));
					long res = tcpClient.reply();
					//					if (i % 50000 == 0) {
					//						System.out.println(">>>lpush node [" + id + "] : " + i + " >>" + res);
					//					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
