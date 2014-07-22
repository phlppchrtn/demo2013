package socket.tcp.io;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import socket.tcp.io.resp.RespClient;

public final class RedisClient implements AutoCloseable {
	private final RespClient tcpClient;

	public RedisClient(String host, int port) {
		tcpClient = new RespClient(host, port);
	}

	//-------------------------------------------------------------------------
	//------------------------------list---------------------------------------
	//-------------------------------------------------------------------------
	//	BLPOP, BRPOP, BRPOPLPUSH, LINDEX, LINSERT, LLEN, LPOP
	//	LPUSH, LPUSHX, LRANGE, LREM, LSET LTRIM, RPOP, RPOPLPUSH, RPUSH, RPUSHX
	//-------------------------------------------------------------------------
	List<String> blpop(long timeout, String... keys) throws IOException {
		String[] args = args(timeout, keys);
		return tcpClient.execArray("blpop", args);
	}

	List<String> brpop(long timeout, String... keys) throws IOException {
		String[] args = args(timeout, keys);
		return tcpClient.execArray("brpop", args);
	}

	String brpoplpush(String source, String destination, long timeout) throws IOException {
		return tcpClient.execBulk("brpoplpush", args(source, destination, String.valueOf(timeout)));
	}

	String lindex(String key, int index) throws IOException {
		return tcpClient.execBulk("lindex", key, String.valueOf(index));
	}

	long llen(String key) throws IOException {
		return tcpClient.execLong("llen", key);
	}

	String lpop(String key) throws IOException {
		return tcpClient.execBulk("lpop", key);
	}

	long lpush(String key, String value) throws IOException {
		return tcpClient.execLong("lpush", key, value);
	}

	long lpushx(String key, String value) throws IOException {
		return tcpClient.execLong("lpushx", key, value);
	}

	String rpop(String key) throws IOException {
		return tcpClient.execBulk("rpop", key);
	}

	long rpush(String key, String value) throws IOException {
		return tcpClient.execLong("rpush", key, value);
	}

	long rpushx(String key, String value) throws IOException {
		return tcpClient.execLong("rpushx", key, value);
	}

	//-------------------------------------------------------------------------
	//-----------------------------/list---------------------------------------
	//-------------------------------------------------------------------------

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

	boolean expire(String key, long seconds) throws IOException {
		return tcpClient.execLong("expire", key, String.valueOf(seconds)) == 1;
	}

	long del(String... keys) throws IOException {
		return tcpClient.execLong("del", keys);
	}

	void flushall() throws IOException {
		tcpClient.execString("flushall");
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

	boolean hset(String key, String field, String value) throws IOException {
		return tcpClient.execLong("hset", key, field, value) == 1;
	}

	void hmset(String key, Map<String, String> map) throws IOException {
		String[] args = new String[map.size() * 2 + 1];
		int i = 0;
		args[i++] = key;
		for (Entry<String, String> entry : map.entrySet()) {
			args[i++] = entry.getKey();
			args[i++] = entry.getValue();
		}
		//System.out.println("args>"+ Arrays.asList(args));
		tcpClient.execString("hmset", args);
	}

	long hincrBy(String key, String field, long increment) throws IOException {
		return tcpClient.execLong("hincrby", key, field, String.valueOf(increment));
	}

	private static String[] args(String key, String... values) {
		String[] args = new String[values.length + 1];
		args[0] = key;
		for (int i = 0; i < values.length; i++) {
			args[i + 1] = values[i];
		}
		return args;
	}

	private String[] args(long timeout, String... keys) {
		String[] args = new String[keys.length + 1];
		for (int i = 0; i < keys.length; i++) {
			args[i] = keys[i];
		}
		args[keys.length] = String.valueOf(timeout);
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
