package socket.tcp.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import socket.tcp.io.impl.GetSetBenchmark;
import socket.tcp.io.resp.RespCommand;
import socket.tcp.io.resp.RespCommandHandler;
import socket.tcp.io.resp.RespProtocol;
import socket.tcp.io.resp.RespServer;

public final class RedisServer implements RespCommandHandler {
	private final RespServer respServer;
	private final Map<String, String> map = new HashMap<>();

	RedisServer(int port) {
		respServer = new RespServer(port, this);
		new Thread(respServer).start();
	}

	public void onCommand(OutputStream out, RespCommand command) throws IOException {
		switch (command.getName().toLowerCase()) {
			case "ping":
				RespProtocol.writeSimpleString(out, "PONG");
				break;
			case "set":
				map.put(command.args()[0], command.args()[1]);
				RespProtocol.writeSimpleString(out, "OK");
				break;
			case "get":
				String value = map.get(command.args()[0]);
				RespProtocol.writeBulkString(out, value);
				break;
			case "flushall":
				map.clear();
				RespProtocol.writeSimpleString(out, "OK");
				break;
			default:
				throw new IllegalArgumentException("command unknown : " + command.getName());
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println(">>redis server....");
		RedisServer server = new RedisServer(6380);
		System.out.println(">>redis server démarré");
		new GetSetBenchmark().playVedis();
		new GetSetBenchmark().playJedis();
		server.toString();
	}
}
