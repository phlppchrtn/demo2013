package io.vertigo.nitro.tcp.io;

import io.vertigo.nitro.tcp.io.impl.GetSetBenchmark;
import io.vertigo.nitro.tcp.io.resp.RespCommand;
import io.vertigo.nitro.tcp.io.resp.RespCommandHandler;
import io.vertigo.nitro.tcp.io.resp.RespProtocol;
import io.vertigo.nitro.tcp.io.resp.RespServer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public final class CopyOfRedisServer implements RespCommandHandler {
	private final RespServer respServer;
	private final Map<String, String> map = new HashMap<>();
	private final Map<String, BlockingDeque<String>> lists = new HashMap<>();

	CopyOfRedisServer(int port) {
		respServer = new RespServer(port, this);
		new Thread(respServer).start();
	}

	public void onCommand(OutputStream out, RespCommand command)
			throws IOException {
		switch (command.getName().toLowerCase()) {
		case "ping":
			RespProtocol.writeSimpleString(out, "PONG");
			break;
		case "set": {
			String key = command.args()[0];
			map.put(key, command.args()[1]);
			RespProtocol.writeSimpleString(out, "OK");
		}
			break;
		case "get": {
			String key = command.args()[0];
			String value = map.get(key);
			RespProtocol.writeBulkString(out, value);
		}
			break;
		case "exists": {
			String key = command.args()[0];
			boolean exists = map.containsKey(key) || lists.containsKey(key);
			RespProtocol.writeLong(out, exists ? 1L : 0L);
		}
			break;
		case "brpoplpush": {
			String key = command.args()[0];
			String key2 = command.args()[1];
			// long key2 = command.args()[2]==;

			BlockingDeque<String> list = lists.get(key);
			if (list == null || list.size() == 0) {
				RespProtocol.writeBulkString(out, null);
			} else {
				String element;
				try {
					element = list.pollFirst(1L, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					element = null;
				}
				if (element != null) {
					BlockingDeque<String> list2 = lists.get(key2);
					if (list2 == null) {
						list2 = new LinkedBlockingDeque<>();
						lists.put(key2, list2);
					}
					list2.addFirst(element);
				}
				RespProtocol.writeBulkString(out, element);
			}
		}
			break;
		case "llen": {
			String key = command.args()[0];
			BlockingDeque<String> list = lists.get(key);
			RespProtocol.writeLong(out, list == null ? 0L : list.size());
		}
			break;
		case "lpop": {
			String key = command.args()[0];
			BlockingDeque<String> list = lists.get(key);
			if (list == null || list.size() == 0) {
				RespProtocol.writeBulkString(out, null);
			} else {
				RespProtocol.writeBulkString(out, list.removeFirst());
			}
		}
			break;
		case "rpop": {
			String key = command.args()[0];
			BlockingDeque<String> list = lists.get(key);
			if (list == null || list.size() == 0) {
				RespProtocol.writeBulkString(out, null);
			} else {
				RespProtocol.writeBulkString(out, list.removeLast());
			}
		}
			break;
		case "lpush": {
			String key = command.args()[0];
			BlockingDeque<String> list = lists.get(key);
			if (list == null) {
				list = new LinkedBlockingDeque<>();
				lists.put(key, list);
			}
			for (int i = 1; i < command.args().length; i++) {
				list.addFirst(command.args()[i]);
			}
			RespProtocol.writeLong(out, 1L * list.size());
		}
			break;
		case "rpush": {
			String key = command.args()[0];
			BlockingDeque<String> list = lists.get(key);
			if (list == null) {
				list = new LinkedBlockingDeque<>();
				lists.put(key, list);
			}
			for (int i = 1; i < command.args().length; i++) {
				list.add(command.args()[i]);
			}
			RespProtocol.writeLong(out, 1L * list.size());
		}
			break;
		case "lpushx": {
			String key = command.args()[0];
			BlockingDeque<String> list = lists.get(key);
			if (list != null) {
				for (int i = 1; i < command.args().length; i++) {
					list.addFirst(command.args()[i]);
				}
			}
			RespProtocol.writeLong(out, list == null ? 0L : list.size());
		}
			break;
		case "rpushx": {
			String key = command.args()[0];
			BlockingDeque<String> list = lists.get(key);
			if (list != null) {
				for (int i = 1; i < command.args().length; i++) {
					list.add(command.args()[i]);
				}
			}
			System.out.println(">>rpushx key:" + key + " , list:" + list);
			RespProtocol.writeLong(out, list == null ? 0L : list.size());
		}
			break;
		case "flushall":
			map.clear();
			lists.clear();
			RespProtocol.writeSimpleString(out, "OK");
			break;
		default:
			RespProtocol.writeError(out, "RESP Command unknown : " + command.getName());
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println(">>redis server....");
		CopyOfRedisServer server = new CopyOfRedisServer(6380);
		System.out.println(">>redis server démarré");
		new GetSetBenchmark().playVedis();
		new GetSetBenchmark().playJedis();
		server.toString();
	}

}
