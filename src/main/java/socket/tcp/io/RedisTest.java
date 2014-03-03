package socket.tcp.io;

import java.io.IOException;

import socket.tcp.AbstractRedisTest;
import socket.tcp.protocol.ReqResp;

public final class RedisTest extends AbstractRedisTest {
	//private final String host = "kasper-redis";
	private final String host = "localhost";
	//private static final int PORT = 6379;
	private final int port = 6379;

	public static void main(String[] args) throws IOException, InterruptedException {
		new RedisTest().testSuite();
	}

	@Override
	protected Runnable createTcpServer() {
		return new TcpServer(port, getCommandHandler());
	}

	@Override
	protected ReqResp createTcpClient() {
		return new TcpClient(host, port);
	}
}
