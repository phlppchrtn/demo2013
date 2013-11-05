package socket.tcp;

import java.io.IOException;

public class TcpMain {
	private static final int PORT = 4444;

	public static void main(String[] args) throws IOException {
		//d�marrage du serveur TCP
		TcpServer tcpServer = new TcpServer(PORT);
		new Thread(tcpServer).start();

		//-------------------------------------------------	
		//D�marrage d'un client
		//-------------------------------------------------	

		try (TcpClient tcpClient = new TcpClient(PORT)) {
			for (int i = 0; i < 25; i++) {
				System.out.println(">>>ping : " + tcpClient.exec(new Command("ping")));
				//System.out.println(">>>pong : " + tcpClient.ask("pong\r\n"));
			}
		}
	}
}
