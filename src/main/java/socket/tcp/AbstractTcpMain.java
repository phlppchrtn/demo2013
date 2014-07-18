package socket.tcp;

import java.io.IOException;

import socket.tcp.protocol.ReqResp;
import socket.tcp.protocol.VCommand;
import socket.tcp.protocol.VCommandHandler;

public abstract class AbstractTcpMain {

	private static final int PORT = 6379;
	private static final String HOST = "localhost";

	protected abstract ReqResp createTcpClient(String host, int port);

	protected abstract Runnable createTcpServer(VCommandHandler commandHandler, int port);

	public static final class Sender implements Runnable {
		//		private final int id;
		private final AbstractTcpMain tcpMain;
		private final int count;

		Sender(int id, AbstractTcpMain tcpMain, int count) {
			//	this.id = id;
			this.tcpMain = tcpMain;
			this.count = count;
		}

		@Override
		public void run() {
			try (ReqResp tcpClient = tcpMain.createTcpClient(HOST, PORT)) {
				for (int i = 0; i < count; i++) {
					long res = tcpClient.exec(new VCommand("ping"));
					//System.out.println(">" + res);
					if (res != 200)
						throw new RuntimeException("erreur dans le retour :" + res);
					//					System.out.println(">>>ping(id=" + id + ") : " + res);
					//System.out.println(">>>pong : " + tcpClient.ask("pong\r\n"));
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected void testSuite() throws InterruptedException {
		//startServer();
		//-------------------------------------------------
		test(1, 500);
		test(10, 50);
		test(20, 25);
		test(50, 10);
	}

	public void test(int threadCount, int count) throws InterruptedException {
		Thread[] threads = new Thread[threadCount];
		long start = System.currentTimeMillis();
		for (int j = 0; j < threadCount; j++) {
			threads[j] = new Thread(new Sender(j, this, count));
			threads[j].start();
		}
		for (int j = 0; j < threadCount; j++) {
			threads[j].join();
		}
		System.out.println("--------------------------------------------------- ");
		System.out.println("----- threads       : " + threadCount);
		System.out.println("----- count/thread  : " + count);
		System.out.println("----- elapsed time  : " + ((System.currentTimeMillis() - start) / 1000) + "s");
		System.out.println("--------------------------------------------------- ");

	}

	//	private void startServer() {
	//		//démarrage du serveur TCP
	//		Runnable tcpServer = createTcpServer(new MyCommandHandler(), PORT);
	//		new Thread(tcpServer).start();
	//	}

	protected static class MyCommandHandler implements VCommandHandler {
		public String onCommand(VCommand command) {
			if ("ping".equals(command.getName())) {
				return "+OK";
			} else if ("pong".equals(command.getName())) {
				return "+OK";
			}
			throw new RuntimeException("Command inconnue :" + command.getName());
		}
	}

	public AbstractTcpMain() {
		super();
	}

}
