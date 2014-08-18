package socket.tcp.nio;

import java.io.IOException;

import sockect.tcp.protocol.ReqResp;
import socket.tcp.AbstractRedisTest;

public final class RedisTest2 extends AbstractRedisTest {
	private static final String host = "kasper-redis";
	//private final String host = "localhost";
	//private static final int PORT = 6379;
	private final int port = 6379;

	public static void main(String[] args) throws IOException, InterruptedException {
		new RedisTest2().testSuite();
	}

	@Override
	protected Runnable createTcpServer() {
		return new TcpServer2(port, getCommandHandler());
	}

	@Override
	protected ReqResp createTcpClient() {
		return new TcpClient2(host, port);
	}
}
