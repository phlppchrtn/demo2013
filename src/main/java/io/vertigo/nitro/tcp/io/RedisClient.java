package io.vertigo.nitro.tcp.io;

import io.vertigo.nitro.tcp.io.resp.RespClient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class RedisClient implements AutoCloseable {
	private final RespClient tcpClient;

	public RedisClient(final String host, final int port) {
		tcpClient = new RespClient(host, port);
	}

	//-------------------------------------------------------------------------
	//------------------------------list---------------------------------------
	//-------------------------------------------------------------------------
	//	BLPOP, BRPOP, BRPOPLPUSH, LINDEX, -LINSERT, LLEN, LPOP
	//	LPUSH, LPUSHX, LRANGE, LREM, -LSET, -LTRIM, RPOP, -RPOPLPUSH, RPUSH, RPUSHX
	//-------------------------------------------------------------------------
	public List<String> blpop(final long timeout, final String... keys) {
		final String[] args = args(timeout, keys);
		return tcpClient.execArray("blpop", args);
	}

	public List<String> brpop(final long timeout, final String... keys) {
		final String[] args = args(timeout, keys);
		return tcpClient.execArray("brpop", args);
	}

	public String brpoplpush(final String source, final String destination, final long timeout) {
		return tcpClient.execBulk("brpoplpush", source, destination, String.valueOf(timeout));
	}

	public String lindex(final String key, final int index) {
		return tcpClient.execBulk("lindex", key, String.valueOf(index));
	}

	public long llen(final String key) {
		return tcpClient.execLong("llen", key);
	}

	public String lpop(final String key) {
		return tcpClient.execBulk("lpop", key);
	}

	public long lpush(final String key, final String value) {
		return tcpClient.execLong("lpush", key, value);
	}

	public long lpushx(final String key, final String value) {
		return tcpClient.execLong("lpushx", key, value);
	}

	public List<String> lrange(final String key, final long start, final long stop) {
		return tcpClient.execArray("lrange", key, String.valueOf(start), String.valueOf(stop));
	}

	public long lrem(final String key, final long count, final String value) {
		return tcpClient.execLong("lrem", key, String.valueOf(count), value);
	}

	public String rpop(final String key) {
		return tcpClient.execBulk("rpop", key);
	}

	public long rpush(final String key, final String value) {
		return tcpClient.execLong("rpush", key, value);
	}

	public long rpushx(final String key, final String value) {
		return tcpClient.execLong("rpushx", key, value);
	}

	//-------------------------------------------------------------------------
	//-----------------------------/list---------------------------------------
	//------------------------------hyperLogLog--------------------------------
	//-------------------------------------------------------------------------
	// PFADD, PFCOUNT, PFMERGE
	//-------------------------------------------------------------------------
	public long pfadd(final String key, final String... elements) {
		return tcpClient.execLong("pfadd", args(key, elements));
	}

	public long pfcount(final String... keys) {
		return tcpClient.execLong("pfcount", keys);
	}

	public void pfmerge(final String destkey, final String... sourcekeys) {
		tcpClient.execString("pfmerge", args(destkey, sourcekeys));
	}

	//-------------------------------------------------------------------------
	//-----------------------------/hyperLogLog--------------------------------
	//------------------------------hash---------------------------------------
	//-------------------------------------------------------------------------
	// HDEL, HEXISTS, HGET, HGETALL, HINCRBY, -HINCRBYFLOAT, HKEYS, HLEN
	// -HMGET -HMSET, -HSCAN, HSET, HSETNX,  HVALS
	//-------------------------------------------------------------------------
	public long hdel(final String key, final String... fields) {
		return tcpClient.execLong("hdel", args(key, fields));
	}

	public boolean hexists(final String key, final String field) {
		return tcpClient.execLong("hexists", key, field) == 1;
	}

	public String hget(final String key, final String field) {
		return tcpClient.execBulk("hget", key, field);
	}

	public Map<String, String> hgetAll(final String key) {
		final List<String> values = tcpClient.execArray("hgetall", key);
		final Map<String, String> map = new HashMap<>();
		for (int i = 0; i < (values.size() / 2); i++) {
			map.put(values.get(2 * i), values.get(2 * i + 1));
		}
		return map;
	}

	public long hincrBy(final String key, final String field, final long increment) {
		return tcpClient.execLong("hincrby", key, field, String.valueOf(increment));
	}

	public Set<String> hkeys(final String key) {
		return new HashSet<>(tcpClient.execArray("hkeys", key));
	}

	public long hlen(final String key) {
		return tcpClient.execLong("hlen", key);
	}

	public boolean hset(final String key, final String field, final String value) {
		return tcpClient.execLong("hset", key, field, value) == 1;
	}

	public boolean hsetnx(final String key, final String field, final String value) {
		return tcpClient.execLong("hsetnx", key, field, value) == 1;
	}

	public void hmset(final String key, final Map<String, String> map) {
		final String[] args = args(key, map);
		tcpClient.execString("hmset", args);
	}

	public List<String> hvals(final String key) {
		return tcpClient.execArray("hvals", key);
	}

	//-------------------------------------------------------------------------
	//-----------------------------/hash---------------------------------------
	//-------------------------------------------------------------------------

	public long append(final String key, final String value) {
		return tcpClient.execLong("append", key, value);
	}

	public String get(final String key) {
		return tcpClient.execBulk("get", key);
	}

	public String set(final String key, final String value) {
		return tcpClient.execString("set", key, value);
	}

	public boolean exists(final String key) {
		return tcpClient.execLong("exists", key) == 1;
	}

	public boolean expire(final String key, final long seconds) {
		return tcpClient.execLong("expire", key, String.valueOf(seconds)) == 1;
	}

	public long del(final String... keys) {
		return tcpClient.execLong("del", keys);
	}

	public void flushAll() {
		tcpClient.execString("flushall");
	}

	public String ping() {
		return tcpClient.execString("ping");
	}

	public String echo(final String message) {
		return tcpClient.execBulk("echo", message);
	}

	public String auth(final String password) {
		return tcpClient.execString("auth", password);
	}

	public long sadd(final String key, final String... members) {
		return tcpClient.execLong("sadd", args(key, members));
	}

	public String spop(final String key) {
		return tcpClient.execBulk("pop", key);
	}

	public Object eval(final String script) {
		return tcpClient.execEval("eval", script, String.valueOf(0));
	}

	public void close() {
		tcpClient.close();
	}

	//------------------

	private static String[] args(final String key, final Map<String, String> map) {
		final String[] args = new String[map.size() * 2 + 1];
		int i = 0;
		args[i++] = key;
		for (final Entry<String, String> entry : map.entrySet()) {
			args[i++] = entry.getKey();
			args[i++] = entry.getValue();
		}
		return args;
	}

	private static String[] args(final String key, final String... values) {
		final String[] args = new String[values.length + 1];
		args[0] = key;
		for (int i = 0; i < values.length; i++) {
			args[i + 1] = values[i];
		}
		return args;
	}

	private static String[] args(final long timeout, final String... keys) {
		final String[] args = new String[keys.length + 1];
		for (int i = 0; i < keys.length; i++) {
			args[i] = keys[i];
		}
		args[keys.length] = String.valueOf(timeout);
		return args;
	}

	//--------------------------------------------------------

}
