package socket.classloader;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public final class TcpCLServer implements Runnable {
	public static final String CHARSET = "UTF-8";

	private final int port;

	TcpCLServer(int port) {
		this.port = port;
	}

	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while (true) {
				Socket socket = serverSocket.accept();
				new Thread(new TcpSocket(socket)).start();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static class TcpSocket implements Runnable {
		private final Socket socket;

		TcpSocket(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			while (true) {
				try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
					String input;
					while ((input = in.readLine()) != null) {
						System.out.println("server : loading class : " + input);
						byte[] classAsBytes = loadClassData(input);
						//----					
						String size = String.valueOf(classAsBytes.length) + "*";
						socket.getOutputStream().write(size.getBytes(CHARSET));
						//----					
						socket.getOutputStream().write(classAsBytes);
						socket.getOutputStream().flush();
						System.out.println("server : send class : " + classAsBytes.length);
					}
				} catch (IOException e) {
					System.out.println(" connection closed : restarting ");
					throw new RuntimeException(e);
				}
			}
		}

		public byte[] loadClassData(String name) throws IOException {
			// Opening the file
			String classAsPath = name.replace('.', '/') + ".class";
			InputStream stream = this.getClass().getClassLoader().getResourceAsStream(classAsPath);
			if (stream == null) {
				throw new RuntimeException("class non chargée :" + classAsPath);
			}
			int size = stream.available();
			byte buff[] = new byte[size];
			DataInputStream in = new DataInputStream(stream);
			// Reading the binary data
			in.readFully(buff);
			in.close();
			return buff;
		}
	}
}
