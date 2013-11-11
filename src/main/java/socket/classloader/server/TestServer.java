package socket.classloader.server;

public class TestServer {
	//private static final int PORT = 6379;
	private static final int PORT = 4444;

	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		new Thread(new TcpCLServer(PORT)).start();
	}
}
