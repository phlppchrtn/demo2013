package socket.tcp;

import java.io.IOException;

import socket.tcp.protocol.ReqResp;
import socket.tcp.protocol.VCommand;
import socket.tcp.protocol.VCommandHandler;

public abstract class AbstractRedisTest {
	private final VCommandHandler commandHandler = new RedisCommandHandler();

	public final void testSuite() throws InterruptedException, IOException {
		//		new Thread(createTcpServer()).start();
		//		Thread.sleep(10);

		//-----
		test(1, 50000);
		test(2, 25000);
		test(5, 10000);
		test(10, 5000);
		test(20, 2500);
		test(50, 1000);
	}

	protected final VCommandHandler getCommandHandler() {
		return commandHandler;
	}

	protected abstract Runnable createTcpServer();

	protected abstract ReqResp createTcpClient();

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
			long res = tcpClient.exec(new VCommand("llen", "test"));
			return total == res;
		}
	}

	private void flushDb() throws IOException {
		try (ReqResp tcpClient = createTcpClient()) {
			long res = tcpClient.exec(new VCommand("flushdb"));
			//System.out.println(">>Text: flushDB :" + res);
		}
	}

	private void monoThread(int count) throws IOException {
		loop("mono", count);
	}

	private void loop(String id, int count) {
		try (ReqResp tcpClient = createTcpClient()) {
			for (int i = 0; i < count; i++) {
				long res = tcpClient.exec(new VCommand("lpush", "test", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaanode[" + id + "] :" + i));
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

	private static class RedisCommandHandler implements VCommandHandler {
		private long datas;

		public String onCommand(VCommand command) {
			if ("flushdb".equals(command.getName())) {
				datas = 0;
			} else if ("llen".equals(command.getName())) {
			} else if ("lpush".equals(command.getName())) {
				datas++;
			} else {
				throw new RuntimeException("Command inconnue :" + command.getName());
			}

			return ":" + datas;
		}
	}
}
