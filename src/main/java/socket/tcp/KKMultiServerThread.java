package socket.tcp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class KKMultiServerThread extends Thread {
	private ServerSocket serverSocket;

	public KKMultiServerThread(ServerSocket serverSocket) {
		super("KKMultiServerThread");
		this.serverSocket = serverSocket;
	}

	public void run() {

		try {

			try (Socket clientSocket = serverSocket.accept()) {
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				String inputLine, outputLine;
				KnockKnockProtocol kkp = new KnockKnockProtocol();
				outputLine = kkp.processInput(null);
				out.println(outputLine);

				while ((inputLine = in.readLine()) != null) {
					outputLine = kkp.processInput(inputLine);
					out.println(outputLine);
					if (outputLine.equals("Bye"))
						break;
				}
				out.close();
				in.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
