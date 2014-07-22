package socket.tcp.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import socket.tcp.io.resp.RespClient;

public final class RedisClient implements AutoCloseable {
	private final RespClient tcpClient;

	public RedisClient(String host, int port) {
		tcpClient = new RespClient(host, port);
	}

	//-------------------------------------------------------------------------
	//------------------------------list---------------------------------------
	//-------------------------------------------------------------------------
	//	BLPOP, BRPOP, BRPOPLPUSH, LINDEX, -LINSERT, LLEN, LPOP
	//	LPUSH, LPUSHX, LRANGE, LREM, -LSET, -LTRIM, RPOP, -RPOPLPUSH, RPUSH, RPUSHX
	//-------------------------------------------------------------------------
	public List<String> blpop(long timeout, String... keys) throws IOException {
		String[] args = args(timeout, keys);
		return tcpClient.execArray("blpop", args);
	}

	public List<String> brpop(long timeout, String... keys) throws IOException {
		String[] args = args(timeout, keys);
		return tcpClient.execArray("brpop", args);
	}

	public String brpoplpush(String source, String destination, long timeout) throws IOException {
		return tcpClient.execBulk("brpoplpush", source, destination, String.valueOf(timeout));
	}

	public String lindex(String key, int index) throws IOException {
		return tcpClient.execBulk("lindex", key, String.valueOf(index));
	}

	public long llen(String key) throws IOException {
		return tcpClient.execLong("llen", key);
	}

	public String lpop(String key) throws IOException {
		return tcpClient.execBulk("lpop", key);
	}

	public long lpush(String key, String value) throws IOException {
		return tcpClient.execLong("lpush", key, value);
	}

	public long lpushx(String key, String value) throws IOException {
		return tcpClient.execLong("lpushx", key, value);
	}

	public List<String> lrange(String key, long start, long stop) throws IOException {
		return tcpClient.execArray("lrange", key, String.valueOf(start), String.valueOf(stop));
	}

	public long lrem(String key, long count, String value) throws IOException {
		return tcpClient.execLong("lrem", key, String.valueOf(count), value);
	}

	public String rpop(String key) throws IOException {
		return tcpClient.execBulk("rpop", key);
	}

	public long rpush(String key, String value) throws IOException {
		return tcpClient.execLong("rpush", key, value);
	}

	public long rpushx(String key, String value) throws IOException {
		return tcpClient.execLong("rpushx", key, value);
	}

	//-------------------------------------------------------------------------
	//-----------------------------/list---------------------------------------
	//------------------------------hash---------------------------------------
	//-------------------------------------------------------------------------
	// HDEL, HEXISTS, HGET, HGETALL, HINCRBY, -HINCRBYFLOAT, HKEYS, HLEN
	// -HMGET -HMSET, -HSCAN, HSET, HSETNX,  HVALS
	//-------------------------------------------------------------------------
	public long hdel(String key, String... fields) throws IOException {
		return tcpClient.execLong("hdel", args(key, fields));
	}

	public boolean hexists(String key, String field) throws IOException {
		return tcpClient.execLong("hexists", key, field) == 1;
	}

	public String hget(String key, String field) throws IOException {
		return tcpClient.execBulk("hget", key, field);
	}

	public Map<String, String> hgetAll(String key) throws IOException {
		List<String> values = tcpClient.execArray("hgetall", key);
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < (values.size() / 2); i++) {
			map.put(values.get(2 * i), values.get(2 * i + 1));
		}
		return map;
	}

	public long hincrBy(String key, String field, long increment) throws IOException {
		return tcpClient.execLong("hincrby", key, field, String.valueOf(increment));
	}

	public Set<String> hkeys(String key) throws IOException {
		return new HashSet<>(tcpClient.execArray("hkeys", key));
	}

	public long hlen(String key) throws IOException {
		return tcpClient.execLong("hlen", key);
	}

	public boolean hset(String key, String field, String value) throws IOException {
		return tcpClient.execLong("hset", key, field, value) == 1;
	}
	
	public boolean hsetnx(String key, String field, String value) throws IOException {
		return tcpClient.execLong("hsetnx", key, field, value) == 1;
	}
	public void hmset(String key, Map<String, String> map) throws IOException {
		String[] args = args(key, map);
		tcpClient.execString("hmset", args);
	}

	public List<String> hvals(String key) throws IOException {
		return tcpClient.execArray("hvals", key);
	}

	//-------------------------------------------------------------------------
	//-----------------------------/hash---------------------------------------
	//-------------------------------------------------------------------------

	public long append(String key, String value) throws IOException {
		return tcpClient.execLong("append", key, value);
	}

	public String get(String key) throws IOException {
		return tcpClient.execBulk("get", key);
	}

	public String set(String key, String value) throws IOException {
		return tcpClient.execString("set", key, value);
	}

	public boolean exists(String key) throws IOException {
		return tcpClient.execLong("exists", key) == 1;
	}

	public boolean expire(String key, long seconds) throws IOException {
		return tcpClient.execLong("expire", key, String.valueOf(seconds)) == 1;
	}

	public long del(String... keys) throws IOException {
		return tcpClient.execLong("del", keys);
	}

	public void flushAll() throws IOException {
		tcpClient.execString("flushall");
	}

	public String ping() throws IOException {
		return tcpClient.execString("ping");
	}

	public String echo(String message) throws IOException {
		return tcpClient.execBulk("echo", message);
	}

	public String auth(String password) throws IOException {
		return tcpClient.execString("auth", password);
	}

	public long sadd(String key, String... members) throws IOException {
		return tcpClient.execLong("sadd", args(key, members));
	}

	public String spop(String key) throws IOException {
		return tcpClient.execBulk("pop", key);
	}
	public Object eval(String script) throws IOException {
		return tcpClient.execEval("eval", script, String.valueOf(0));
	}
	
	public void close() throws IOException {
		tcpClient.close();
	}

	//------------------

	private static String[] args(String key, Map<String, String> map) {
		String[] args = new String[map.size() * 2 + 1];
		int i = 0;
		args[i++] = key;
		for (Entry<String, String> entry : map.entrySet()) {
			args[i++] = entry.getKey();
			args[i++] = entry.getValue();
		}
		return args;
	}

	private static String[] args(String key, String... values) {
		String[] args = new String[values.length + 1];
		args[0] = key;
		for (int i = 0; i < values.length; i++) {
			args[i + 1] = values[i];
		}
		return args;
	}

	private static String[] args(long timeout, String... keys) {
		String[] args = new String[keys.length + 1];
		for (int i = 0; i < keys.length; i++) {
			args[i] = keys[i];
		}
		args[keys.length] = String.valueOf(timeout);
		return args;
	}

	//--------------------------------------------------------

}
