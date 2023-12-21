package io.vertigo.redis.alphaserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.vertigo.redis.resp.RespCommand;
import io.vertigo.redis.resp.RespCommandHandler;
import io.vertigo.redis.resp.RespServer;
import io.vertigo.redis.resp.RespWriter;

public final class RedisServer implements RespCommandHandler {
	private final RespServer respServer;
	private final Map<String, Map<String, String>> hashes = new HashMap<>();

	private boolean exec = false;
	private List<RespCommand> multi = null;
	private final Map<String, String> map = new HashMap<>();
	private final Map<String, RedisQueue> lists = new HashMap<>();

	public RedisServer(final int port) {
		respServer = new RespServer(port, this);
	}

	public void start() {
		new Thread(respServer).start();
	}

	@Override
	public void onCommand(final RespWriter writer, final RespCommand command) throws IOException {
		if (!exec && multi != null && !"exec".equals(command.getName())) {
			multi.add(command);
			writer.writeSimpleString("QUEUED");
			//			System.out.println("     |--- command:" + command.getName());
			return;
		}
		System.out.println("--- command:" + command.getName());
		for (final String arg : command.args()) {
			System.out.println("   |-arg :" + arg);
		}
		switch (command.getName()) {
			//-------------------------Connection----------------------------------
			case "ping":
				writer.writeSimpleString("PONG");
				break;
			case "echo":
				final String message = command.args()[0];
				writer.writeBulkString(message);
				break;
			//-------------------------Hash------------------------------------
			case "hlen": {
				final String key = command.args()[0];
				final Map<String, String> hash = hashes.get(key);
				writer.writeLong(1L * (hash == null ? 0 : hash.size()));
			}
				break;
			case "hmset": {
				final String key = command.args()[0];
				final Map<String, String> newHash = new HashMap<>();
				for (int i = 1; i < command.args().length; i = i + 2) {
					newHash.put(command.args()[i], command.args()[i + 1]);
				}
				final Map<String, String> hash = hashes.get(key);
				if (hash != null) {
					hash.putAll(newHash);
				} else {
					hashes.put(key, newHash);
				}
				writer.writeOK();
			}
				break;
			case "hget": {
				final String key = command.args()[0];
				final String field = command.args()[1];
				final Map<String, String> hash = hashes.get(key);
				writer.writeBulkString(hash == null ? null : hash.get(field));
			}
				break;
			case "hgetall": {
				final String key = command.args()[0];
				final Map<String, String> getAll = hashes.get(key);
				writer.write("*").write(2 * getAll.size()).writeLN();
				for (final Entry<String, String> entry : getAll.entrySet()) {
					writer.writeSimpleString(entry.getKey());
					writer.writeBulkString(entry.getValue());
				}
			}
				break;
			case "hexists": {
				final String key = command.args()[0];
				final String field = command.args()[1];
				final Map<String, String> hash = hashes.get(key);
				writer.writeLong(hash == null ? 0L : hash.containsKey(field) ? 1L : 0L);
			}
				break;
			case "hdel": {
				final String key = command.args()[0];
				final Map<String, String> hash = hashes.get(key);
				long deleted = 0L;
				if (hash != null) {
					for (int i = 1; i < command.args().length; i++) {
						final String field = command.args()[i];
						if (hash.containsKey(field)) {
							hash.remove(field);
							deleted++;
						}
					}
				}

				writer.writeLong(deleted);
			}
				break;
			case "hincrby": {
				final String key = command.args()[0];
				final String field = command.args()[1];
				final long increment = Long.valueOf(command.args()[2]);
				Map<String, String> hash = hashes.get(key);
				if (hash == null) {
					hash = new HashMap<>();
					hashes.put(key, hash);
				}
				if (!hash.containsKey(field)) {
					hash.put(field, "0");
				}
				final long value = Long.valueOf(hash.get(field)) + increment;
				hash.put(field, "" + value);

				writer.writeLong(value);
			}
				break;
			case "hsetnx": {
				final String key = command.args()[0];
				final String field = command.args()[1];
				final String value = command.args()[2];
				long added = 0;
				Map<String, String> hash = hashes.get(key);
				if (hash == null) {
					hash = new HashMap<>();
					hashes.put(key, hash);
				}
				if (!hash.containsKey(field)) {
					hash.put(field, value);
					added = 1;
				}
				writer.writeLong(added);
			}
				break;
			case "hset": {
				final String key = command.args()[0];
				final String field = command.args()[1];
				final String value = command.args()[2];
				long newField = 0;
				Map<String, String> hash = hashes.get(key);
				if (hash == null) {
					hash = new HashMap<>();
					hashes.put(key, hash);
				}
				if (!hash.containsKey(field)) {
					newField = 1;
				}
				hash.put(field, value);

				writer.writeLong(newField);
			}
				break;
			//-------------------------Hash------------------------------------
			case "set": {
				final String key = command.args()[0];
				map.put(key, command.args()[1]);
				writer.writeOK();
			}
				break;
			case "get": {
				final String key = command.args()[0];
				final String value = map.get(key);
				writer.writeBulkString(value);
			}
				break;
			case "exists": {
				final String key = command.args()[0];
				final boolean exists = map.containsKey(key) || lists.containsKey(key);
				writer.writeLong(exists ? 1L : 0L);
			}
				break;
			case "brpop": {
				final String key = command.args()[0];

				final RedisQueue list = lists.get(key);
				if (list == null || list.size() == 0) {
					writer.writeBulkString(null);
				} else {
					final String element = list.brpop();
					writer
							.write("*2").writeLN()
							.writeSimpleString(key)
							.writeBulkString(element);
				}
			}
				break;
			case "blpop": {
				final String key = command.args()[0];

				final RedisQueue list = lists.get(key);
				if (list == null || list.size() == 0) {
					writer.writeBulkString(null);
				} else {
					final String element = list.blpop();
					writer
							.write("*2").writeLN()
							.writeSimpleString(key)
							.writeBulkString(element);
				}
			}
				break;
			case "brpoplpush": {
				final String key = command.args()[0];
				final String key2 = command.args()[1];
				// long key2 = command.args()[2]==;

				final RedisQueue list = lists.get(key);
				if (list == null || list.size() == 0) {
					writer.writeBulkString(null);
				} else {
					final String element = list.blpop();
					if (element != null) {
						RedisQueue list2 = lists.get(key2);
						if (list2 == null) {
							list2 = new RedisQueue();
							lists.put(key2, list2);
						}
						list2.lpushx(element);
					}
					writer.writeBulkString(element);
				}
			}
				break;
			case "llen": {
				final String key = command.args()[0];
				final RedisQueue list = lists.get(key);
				writer.writeLong(list == null ? 0L : list.size());
			}
				//			{
				//				final String key = command.args()[0];
				//				final List<String> list = lists.get(key);
				//				RespProtocol.writeLong(out, list == null ? 0L : list.size());
				//			}
				break;
			case "lrem": {
				final String key = command.args()[0];
				final int count = Integer.valueOf(command.args()[1]);
				final String value = command.args()[2];
				final RedisQueue list = lists.get(key);

				final int removed = list.lrem(count, value);
				writer.writeLong(removed * 1L);
			}
				break;
			case "lindex": {
				final String key = command.args()[0];
				final int idx = Integer.valueOf(command.args()[1]);
				final RedisQueue list = lists.get(key);
				if (list == null || list.size() == 0) {
					writer.writeBulkString(null);
				} else {
					writer.writeBulkString(list.lindex(idx));
				}
			}
				break;
			case "lpop": {
				final String key = command.args()[0];
				final RedisQueue list = lists.get(key);
				if (list == null || list.size() == 0) {
					writer.writeBulkString(null);
				} else {
					writer.writeBulkString(list.lpop());
				}
			}
				break;
			case "rpop": {
				final String key = command.args()[0];
				final RedisQueue list = lists.get(key);
				if (list == null || list.size() == 0) {
					writer.writeBulkString(null);
				} else {
					writer.writeBulkString(list.rpop());
				}
			}
				break;
			case "lpush": {
				final String key = command.args()[0];
				RedisQueue list = lists.get(key);
				if (list == null) {
					list = new RedisQueue();
					lists.put(key, list);
				}
				for (int i = 1; i < command.args().length; i++) {
					list.lpushx(command.args()[i]);
				}
				writer.writeLong(1L * list.size());
			}
				break;
			case "lrange": {
				final String key = command.args()[0];
				final int start = Integer.valueOf(command.args()[1]);
				final int stop = Integer.valueOf(command.args()[2]);

				final RedisQueue list = lists.get(key);
				if (list == null) {
					writer.write("*").write(0).writeLN();
				} else {
					final List<String> elements = list.lrange(start, stop);
					writer.write("*").write(elements.size()).writeLN();
					for (final String element : elements) {
						writer.writeBulkString(element);
					}
				}
			}
				break;
			case "rpush": {
				final String key = command.args()[0];
				RedisQueue list = lists.get(key);
				if (list == null) {
					list = new RedisQueue();
					lists.put(key, list);
				}
				for (int i = 1; i < command.args().length; i++) {
					list.rpushx(command.args()[i]);
				}
				writer.writeLong(1L * list.size());
			}
				break;
			case "lpushx": {
				final String key = command.args()[0];
				final RedisQueue list = lists.get(key);
				if (list != null) {
					for (int i = 1; i < command.args().length; i++) {
						list.lpushx(command.args()[i]);
					}
				}
				writer.writeLong(list == null ? 0L : list.size());
			}
				break;
			case "rpushx": {
				final String key = command.args()[0];
				final RedisQueue list = lists.get(key);
				if (list != null) {
					for (int i = 1; i < command.args().length; i++) {
						list.rpushx(command.args()[i]);
					}
				}
				System.out.println(">>rpushx key:" + key + " , list:" + list);
				writer.writeLong(list == null ? 0L : list.size());
			}
				break;
			case "flushall":
				map.clear();
				lists.clear();
				writer.writeOK();
				break;
			case "multi":
				exec = false;
				multi = new ArrayList<>();
				writer.writeOK();
				break;
			case "exec":
				//exec
				exec = true;
				writer.write("*").write(multi.size()).writeLN();
				for (final RespCommand multiCommand : multi) {
					onCommand(writer, multiCommand);
				}
				exec = true;
				break;
			default:
				writer.writeError("RESP Command unknown : " + command.getName());
		}
		writer.flush();
	}

	public static void main(final String[] args) throws Exception {
		System.out.println(">>redis server....");
		new RedisServer(6379).start();
	}

	//		CopyOfRedisServer server = new CopyOfRedisServer(6380);
	//		System.out.println(">>redis server démarré");
	//		new GetSetBenchmark().playVedis();
	//		new GetSetBenchmark().playJedis();
	//		server.toString();
	//	private final Map<String, RespCommandHandler> commands = new HashMap<>();
	//
	//	private void init() {
	//		//1. search all methods
	//		for (final Method method : ClassUtil.getAllMethods(this.getClass(), RedisCommand.class)) {
	//			method.getParameterTypes();
	//
	//			//2. For each method register a listener
	//			commands.put(method.getName().toLowerCase(), new RespCommandHandler() {
	//				@Override
	//				public void onCommand(final RespWriter writer, final RespCommand command) throws IOException {
	//					method.invoke(obj, args);
	//				}
	//			});
	//		}
	//	}
}
