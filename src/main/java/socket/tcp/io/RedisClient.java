package socket.tcp.io;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

public final class RedisClient implements AutoCloseable {
	private final TcpClient tcpClient;

	public RedisClient(String host, int port) {
		tcpClient = new TcpClient(host, port);
	}

	long append(String key, String value) throws IOException {
		return tcpClient.execLong("append", key, value);
	}

	String get(String key) throws IOException {
		return tcpClient.execBulk("get", key);
	}

	String set(String key, String value) throws IOException {
		return tcpClient.execString("set", key, value);
	}

	boolean exists(String key) throws IOException {
		return tcpClient.execLong("exists", key) == 1;
	}

	long del(String... keys) throws IOException {
		return tcpClient.execLong("del", keys);
	}

	void flushall() throws IOException {
		tcpClient.execString("flushall");
	}

	long lpush(String key, String value) throws IOException {
		return tcpClient.execLong("lpush", key, value);
	}

	long lpushx(String key, String value) throws IOException {
		return tcpClient.execLong("lpushx", key, value);
	}

	long rpushx(String key, String value) throws IOException {
		return tcpClient.execLong("rpushx", key, value);
	}

	long rpush(String key, String value) throws IOException {
		return tcpClient.execLong("rpush", key, value);
	}

	String ping() throws IOException {
		return tcpClient.execString("ping");
	}

	String echo(String message) throws IOException {
		return tcpClient.execBulk("echo", message);
	}

	//	Set<String> keys(String pattern) throws IOException {
	//		return ;
	//	}

	String auth(String password) throws IOException {
		return tcpClient.execString("auth", password);
	}

	long llen(String key) throws IOException {
		return tcpClient.execLong("llen", key);
	}

	String rpop(String key) throws IOException {
		return tcpClient.execBulk("rpop", key);
	}

	String lpop(String key) throws IOException {
		return tcpClient.execBulk("lpop", key);
	}

	public void close() {
		tcpClient.close();
	}

	//------------------
	boolean hexists(String key, String field) throws IOException {
		return tcpClient.execLong("hexists", key, field) == 1;
	}

	String hget(String key, String field) throws IOException {
		return tcpClient.execBulk("hget", key, field);
	}

	long hdel(String key, String... fields) throws IOException {
		return tcpClient.execLong("hdel", args(key, fields));
	}

	long hlen(String key) throws IOException {
		return tcpClient.execLong("hlen", key);
	}

	String hmset(String key, Map<String, String> map) throws IOException {
		String[] args = new String[map.size() * 2 + 1];
		int i = 0;
		args[i++] = key;
		for (Entry<String, String> entry : map.entrySet()) {
			args[i++] = entry.getKey();
			args[i++] = entry.getValue();
		}
		//System.out.println("args>"+ Arrays.asList(args));
		return tcpClient.execString("hmset", args);
	}

	long hincrBy(String key, String field, long increment) throws IOException {
		return tcpClient.execLong("hincrby", key, field, Long.valueOf(increment).toString());
	}

	private static String[] args(String key, String... values) {
		String[] args = new String[values.length + 1];
		args[0] = key;
		for (int i = 0; i < values.length; i++) {
			args[i + 1] = values[i];
		}
		return args;
	}

	//--------------------------------------------------------
	long sadd(String key, String... members) throws IOException {
		return tcpClient.execLong("sadd", args(key, members));
	}

	String spop(String key) throws IOException {
		return tcpClient.execBulk("pop", key);
	}
}
