package socket.tcp;

import java.io.IOException;

import socket.tcp.io.TcpClient;
import socket.tcp.nio.TcpServer2;
import socket.tcp.protocol.Command;
import socket.tcp.protocol.ReqResp;

public final class TcpMain {
	private final int port = 4444;
	private final String host = "localhost";

	public static void main(String[] args) throws IOException, InterruptedException {
		new TcpMain().test(10, 50000);
	}

	Runnable createTcpServer() {
		return new TcpServer2(port);
	}

	ReqResp createTcpClient() {
		return new TcpClient(host, port);
	}

	public void test(int threadCount, int count) throws InterruptedException {
		//démarrage du serveur TCP
		Runnable tcpServer = createTcpServer();
		new Thread(tcpServer).start();
		//-------------------------------------------------
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

	public static final class Sender implements Runnable {
		private final int id;
		private final TcpMain tcpMain;
		private final int count;

		Sender(int id, TcpMain tcpMain, int count) {
			this.id = id;
			this.tcpMain = tcpMain;
			this.count = count;
		}

		@Override
		public void run() {
			try (ReqResp tcpClient = tcpMain.createTcpClient()) {
				for (int i = 0; i < count; i++) {
					long res = tcpClient.exec(new Command("ping"));
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
}
