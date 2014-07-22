package socket.tcp.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class TestRedisClient {
	private final String host = "pub-redis-15190.us-east-1-3.4.ec2.garantiadata.com";
	//private final String host = "kasper-redis";
	//private final String host = "localhost";
	//private static final int port = 6379;
	private final int port = 15190;

	//	private final TcpClient tcpClient = new TcpClient("localhost", 6379);

	//	public static void main(String[] args) throws Exception {
	//		new TestRedisClient().test();
	//	}
	private RedisClient redis;

	@Before
	public void before() throws IOException {
		redis = new RedisClient(host, port);
		redis.auth("kleegroup");
		redis.flushAll();
	}

	@After
	public void after() throws IOException {
		redis.close();
	}

	//del, exists, expire,
	@Test
	public void testKeys() throws Exception {
		Assert.assertEquals(0, redis.del("left", "right"));
		redis.set("left", "gauche");
		redis.set("right", "droite");
		Assert.assertTrue(redis.exists("left"));
		Assert.assertTrue(redis.exists("right"));
		Assert.assertEquals(2, redis.del("left", "right"));
		Assert.assertFalse(redis.exists("left"));
		Assert.assertFalse(redis.exists("right"));
		//---
		redis.lpush("alert", "myAlert");
		Assert.assertEquals(1, redis.llen("alert"));
		Assert.assertTrue(redis.expire("alert", 1)); //2s
		Thread.sleep(500);
		Assert.assertEquals(1, redis.llen("alert"));
		Thread.sleep(2000);
		Assert.assertEquals(0, redis.llen("alert"));
	}

	//append, get
	@Test
	public void testString() throws Exception {
		Assert.assertEquals(null, redis.get("code"));
		Assert.assertFalse(redis.exists("code"));
		redis.append("code", "1");
		redis.append("code", "4");
		redis.append("code", "9");
		redis.append("code", "2");
		Assert.assertEquals("1492", redis.get("code"));
		Assert.assertTrue(redis.exists("code"));
	}

	@Test
	public void testList() throws Exception {
		Assert.assertEquals(0, redis.llen("actors"));
		redis.lpush("actors", "marlon");
		redis.lpushx("actors", "harrison");
		redis.lpush("actors", "clint");
		Assert.assertEquals(3, redis.llen("actors"));
		Assert.assertEquals("clint", redis.lpop("actors"));
		Assert.assertEquals(2, redis.llen("actors"));
		Assert.assertEquals("marlon", redis.rpop("actors"));
		Assert.assertEquals(1, redis.llen("actors"));
		redis.rpushx("actors", "faye");
		Assert.assertEquals("faye", redis.rpop("actors"));
		redis.rpush("actors", "james");
		Assert.assertEquals("james", redis.rpop("actors"));

		Assert.assertEquals(0, redis.rpushx("movies", "the birds"));
		Assert.assertFalse(redis.exists("movies"));
		Assert.assertEquals(0, redis.lpushx("movies", "the birds"));
		Assert.assertFalse(redis.exists("movies"));
		//----
		redis.lpush("europe", "germany");
		redis.lpush("europe", "france");
		redis.lpush("europe", "italy");
		redis.lpush("countries", "japan");
		redis.lpush("countries", "usa");
		redis.lpush("countries", "argentina");
		Assert.assertEquals(3, redis.llen("europe"));
		Assert.assertEquals(3, redis.llen("countries"));
		redis.brpoplpush("europe", "countries", 1);
		Assert.assertEquals(2, redis.llen("europe"));
		Assert.assertEquals(4, redis.llen("countries"));
		redis.brpoplpush("europe", "countries", 1);
		Assert.assertEquals(1, redis.llen("europe"));
		Assert.assertEquals(5, redis.llen("countries"));
		redis.brpoplpush("europe", "countries", 1);
		Assert.assertEquals(0, redis.llen("europe"));
		Assert.assertEquals(6, redis.llen("countries"));
		Assert.assertEquals(null, redis.brpoplpush("europe", "countries", 1));
		//----test : blpop, brpop
		List<String> list;
		redis.lpush("europe", "germany");
		redis.lpush("europe", "france");
		redis.lpush("europe", "italy");
		list = redis.blpop(1, "europe");
		Assert.assertEquals(2, list.size());
		Assert.assertEquals("europe", list.get(0));
		Assert.assertEquals("italy", list.get(1));

		list = redis.brpop(1, "europe");
		Assert.assertEquals(2, list.size());
		Assert.assertEquals("europe", list.get(0));
		Assert.assertEquals("germany", list.get(1));

		//----test :lindex
		redis.flushAll();
		redis.lpush("europe", "germany");
		redis.lpush("europe", "france");
		redis.lpush("europe", "italy");
		Assert.assertEquals("france", redis.lindex("europe", 1));
		//------
		Assert.assertEquals(1, redis.lpush("mylist", "hello"));
		Assert.assertEquals(2, redis.lpush("mylist", "hello"));
		Assert.assertEquals(3, redis.lpush("mylist", "foo"));
		Assert.assertEquals(4, redis.lpush("mylist", "hello"));
		Assert.assertEquals(5, redis.lpush("mylist", "hello"));
		Assert.assertEquals(2, redis.lrem("mylist", -2, "hello"));
		Assert.assertEquals("foo", redis.rpop("mylist"));
	}

	@Test
	public void testHash() throws Exception {
		Assert.assertEquals(0, redis.hlen("user/1"));
		Map<String, String> map = new HashMap<>();
		map.put("firstname", "john");
		map.put("lastname", "doe");
		redis.hmset("user/1", map);
		Assert.assertEquals(2, redis.hlen("user/1"));
		Assert.assertEquals("john", redis.hget("user/1", "firstname"));
		Assert.assertTrue(redis.hexists("user/1", "firstname"));
		Assert.assertFalse(redis.hexists("user/1", "weight"));
		Assert.assertEquals(2, redis.hlen("user/1"));
		Assert.assertEquals(2, redis.hdel("user/1", "firstname", "lastname"));
		Assert.assertEquals(0, redis.hlen("user/1"));
		//-
		Assert.assertEquals(5, redis.hincrBy("user/1", "hit", 5));
		Assert.assertEquals(20, redis.hincrBy("user/1", "hit", 15));
		//--
		Assert.assertTrue(redis.hsetnx("user/2", "firstname", "john"));
		Assert.assertTrue(redis.hset("user/2", "lastname", "ford"));
		Assert.assertFalse(redis.hset("user/2", "lastname", "ford"));
		Assert.assertFalse(redis.hset("user/2", "lastname", "william"));
		Assert.assertFalse(redis.hsetnx("user/2", "firstname", "henry")); //not modified
		Assert.assertEquals("john", redis.hget("user/2", "firstname")); 

		Map<String, String> user2 = redis.hgetAll("user/2");
		Assert.assertEquals(2, user2.size());
		Assert.assertEquals("william", user2.get("lastname"));
		Assert.assertEquals("john", user2.get("firstname"));

		Set<String> keys = redis.hkeys("user/2");
		Assert.assertEquals(2, keys.size());
		Assert.assertTrue(keys.contains("lastname"));
		Assert.assertTrue(keys.contains("firstname"));

		List<String> values = redis.hvals("user/2");
		Assert.assertEquals(2, values.size());
		Assert.assertTrue(values.contains("william"));
		Assert.assertTrue(values.contains("john"));

	}

	@Test
	public void testConnection() throws Exception {
		Assert.assertEquals("PONG", redis.ping());
		Assert.assertEquals("hello", redis.echo("hello"));
	}
}
