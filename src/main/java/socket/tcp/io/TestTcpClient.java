package socket.tcp.io;

import java.io.IOException;

import socket.tcp.protocol.VCommand;

public final class TestTcpClient {
	private final TcpClient tcpClient = new TcpClient("localhost", 6379);

	public static void main(String[] args) throws IOException {
		TestTcpClient test = new TestTcpClient();
		test.flushall();
		test.lpush("actors", "marlon");
		test.lpush("actors", "clint");
		String actor = test.lpop("actors");
		System.out.println("lpop actors >" + actor);
		System.out.println("llen actors >" + test.llen("actors"));
	}

	String flushall() throws IOException {
		return tcpClient.execString(new VCommand("flushall"));
	}

	long lpush(String key, String... values) throws IOException {
		String[] args = new String[values.length + 1];
		args[0] = key;
		for (int i = 0; i < values.length; i++) {
			args[i + 1] = values[i];
		}
		return tcpClient.execLong(new VCommand("lpush", args));
	}

	long llen(String key) throws IOException {
		return tcpClient.execLong(new VCommand("llen", key));
	}

	long lpushx(String key, String value) throws IOException {
		return tcpClient.execLong(new VCommand("lpushx", key, value));
	}

	String lpop(String key) throws IOException {
		return tcpClient.execBulk(new VCommand("lpop", key));
	}
}
