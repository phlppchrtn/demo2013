package socket.tcp;

import java.io.IOException;

import socket.tcp.io.TcpClient;
import socket.tcp.io.TcpServer;
import socket.tcp.protocol.ReqResp;

public final class RedisTest extends AbstractRedisTest {
	//private static final String HOST = "kasper-redis";
	private final String host = "localhost";
	//private static final int PORT = 6379;
	private final int port = 6379;

	public static void main(String[] args) throws IOException, InterruptedException {
		new RedisTest().testSuite();
	}

	Runnable createTcpServer() {
		return new TcpServer(port, getCommandHandler());
	}

	ReqResp createTcpClient() {
		return new TcpClient(host, port);
	}
}
