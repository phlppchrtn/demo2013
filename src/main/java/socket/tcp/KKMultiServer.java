package socket.tcp;
import java.io.IOException;
import java.net.ServerSocket;

public class KKMultiServer {
	public static void main(String[] args) throws IOException {
		boolean listening = true;

		try (ServerSocket serverSocket = new ServerSocket(4444)) {
			while (listening)
				new KKMultiServerThread(serverSocket).start();
		}
	}
}
