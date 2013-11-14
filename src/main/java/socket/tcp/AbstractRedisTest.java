package socket.tcp;

import java.io.IOException;

import socket.tcp.protocol.Command;
import socket.tcp.protocol.ReqResp;

public abstract class AbstractRedisTest {
	//	//private static final String HOST = "kasper-redis";
	//	private final String host = "localhost";
	//	//private static final int PORT = 6379;
	//	private final int port = 6379;
	//
	//	public static void main(String[] args) throws IOException, InterruptedException {
	//		new AbstractRedisTest().testSuite();
	//	}

	public final void testSuite() throws InterruptedException, IOException {
		//		new Thread(createTcpServer()).start();
		//		Thread.sleep(10);

		//-----
		test(1, 5000);
		test(2, 2500);
		test(5, 1000);
		test(10, 500);
		test(20, 250);
		test(50, 100);
	}

	abstract Runnable createTcpServer();

	abstract ReqResp createTcpClient();

	private final void test(int threadCount, int count) throws IOException, InterruptedException {
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

	private boolean check(int total) throws IOException {
		try (ReqResp tcpClient = createTcpClient()) {
			long res = tcpClient.exec(new Command("llen", "test"));
			return total == res;
		}
	}

	private void flushDb() throws IOException {
		try (ReqResp tcpClient = createTcpClient()) {
			long res = tcpClient.exec(new Command("flushdb"));
			//System.out.println(">>Text: flushDB :" + res);
		}
	}

	private void monoThread(int count) throws IOException {
		loop("mono", count);
	}

	private void loop(String id, int count) {
		try (ReqResp tcpClient = createTcpClient()) {
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

	private void multiThread(int threadCount, int count) throws IOException, InterruptedException {
		Thread[] threads = new Thread[threadCount];
		for (int j = 0; j < threadCount; j++) {
			threads[j] = new Thread(new Sender("" + j, count, this));
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
	public static final class Sender implements Runnable {
		private final String id;
		private final int count;
		private final AbstractRedisTest redis;

		Sender(String id, int count, AbstractRedisTest redis) {
			this.id = id;
			this.count = count;
			this.redis = redis;
		}

		@Override
		public void run() {
			redis.loop(id, count);
		}
	}
}
