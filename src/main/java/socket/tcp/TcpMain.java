package socket.tcp;

import java.io.IOException;

public class TcpMain {
	private static final int PORT = 4444;

	public static void main(String[] args) throws IOException {
		//démarrage du serveur TCP
		TcpServer tcpServer = new TcpServer(PORT);
		new Thread(tcpServer).start();

		for (int j = 0; j < 20; j++) {
			new Thread(new Sender("" + j)).start();
		}
	}

	public static class Sender implements Runnable {
		private final String id;

		Sender(String id) {
			this.id = id;
		}

		@Override
		public void run() {
			try (TcpClient2 tcpClient = new TcpClient2(PORT)) {
				for (int i = 0; i < 5; i++) {
					tcpClient.exec(new Command("ping"));
					System.out.println(">>>ping(id=" + id + ") : " + tcpClient.reply());
					//System.out.println(">>>pong : " + tcpClient.ask("pong\r\n"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
