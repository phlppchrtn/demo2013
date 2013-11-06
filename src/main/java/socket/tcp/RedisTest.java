package socket.tcp;

import java.io.IOException;

public class RedisTest {
	private static final int PORT = 6379;

	public static void main(String[] args) throws IOException, InterruptedException {
		//multiThread(10, 10000);
		monoThread(100000);
	}

	private static void monoThread(int count) throws IOException {
		try (TcpClient2 tcpClient = new TcpClient2(PORT)) {
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

	private static void multiThread(int threadCount, int count) {
		Thread[] threads = new Thread[threadCount];
		for (int j = 0; j < threadCount; j++) {
			threads[j] = new Thread(new Sender("" + j, count));
			threads[j].start();
		}
		//		//Thread.sleep(50000);	
		//		for (int j = 0; j < 10; j++) {
		//			threads[j].join();
		//		}
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
			try (TcpClient2 tcpClient = new TcpClient2(PORT)) {
				for (int i = 0; i < count; i++) {
					tcpClient.exec(new Command("lpush", "test", "node[" + id + "] :" + i));
					long res = tcpClient.reply();
					if (i % 49999 == 0) {
						System.out.println(">>>lpush node [" + id + "] : " + i + " >>" + res);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
