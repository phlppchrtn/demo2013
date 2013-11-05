package socket.tcp;

import java.io.IOException;

public class RedisTest {
	private static final int PORT = 6379;

	public static void main(String[] args) throws IOException {
		//-------------------------------------------------	
		//Démarrage d'un client
		//-------------------------------------------------	
		try (TcpClient2 tcpClient = new TcpClient2(PORT)) {
			for (int i = 0; i < 10; i++) {
				tcpClient.exec(new Command("lpush", "test", "titi" + i));
				System.out.println(">>>lpush : " + tcpClient.reply());
			}
		}
	}
}
