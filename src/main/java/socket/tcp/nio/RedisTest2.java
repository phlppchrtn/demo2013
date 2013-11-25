package socket.tcp.nio;

import java.io.IOException;

import socket.tcp.AbstractRedisTest;
import socket.tcp.protocol.ReqResp;

public final class RedisTest2 extends AbstractRedisTest {
	//private static final String HOST = "kasper-redis";
	private final String host = "localhost";
	//private static final int PORT = 6379;
	private final int port = 6379;

	public static void main(String[] args) throws IOException, InterruptedException {
		new RedisTest2().testSuite();
	}

	protected Runnable createTcpServer() {
		return new TcpServer2(port, getCommandHandler());
	}

	protected ReqResp createTcpClient() {
		return new TcpClient2(host, port);
	}
}
