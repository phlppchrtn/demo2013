package socket.tcp;

import java.io.IOException;

public class RedisTest {
	private static final int PORT = 6379;

	public static void main(String[] args) throws IOException {
		//-------------------------------------------------	
		//Démarrage d'un client
		//-------------------------------------------------	

		try (TcpClient tcpClient = new TcpClient(PORT)) {
			for (int i = 0; i < 1000; i++) {
				System.out.println(">>>lpush : " + tcpClient.exec(new Command("lpush", "test", "toto" + i)));
			}
		}
	}
}
