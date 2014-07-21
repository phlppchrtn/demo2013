package socket.tcp.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public final class TestRedisClient {
	//private final String host = "pub-redis-15190.us-east-1-3.4.ec2.garantiadata.com";
	private final String host = "localhost";
	private static final int port = 6379;

	//	private final int port = 6379;
	//private final int port = 15190;
	//	private final TcpClient tcpClient = new TcpClient("localhost", 6379);

	public static void main(String[] args) throws IOException {
		new TestRedisClient().test();
	}

	@Test
	public void test() throws IOException {
		try (RedisClient redis = new RedisClient(host, port)) {
			//test.auth("kleegroup");
			redis.flushall();
			Assert.assertEquals("PONG", redis.ping());
			Assert.assertEquals("hello", redis.echo("hello"));

			//---get | exists | append
			Assert.assertEquals(null, redis.get("code"));
			Assert.assertFalse(redis.exists("code"));
			redis.append("code", "1");
			redis.append("code", "4");
			redis.append("code", "9");
			redis.append("code", "2");
			Assert.assertEquals("1492", redis.get("code"));
			Assert.assertTrue(redis.exists("code"));
			//---
			Assert.assertEquals(0, redis.del("left", "right"));
			redis.set("left", "gauche");
			redis.set("right", "droite");
			Assert.assertEquals(2, redis.del("left", "right"));
			//---
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
			//---

			//---
			Assert.assertEquals(0, redis.hlen("user/1"));
			Map<String, String> map = new HashMap<>();
			map.put("first name", "john");
			map.put("last name", "doe");
			redis.hmset("user/1", map);
			Assert.assertEquals(2, redis.hlen("user/1"));
			Assert.assertEquals("john", redis.hget("user/1", "first name"));
			Assert.assertTrue(redis.hexists("user/1", "first name"));
			Assert.assertFalse(redis.hexists("user/1", "weight"));
			Assert.assertEquals(2, redis.hlen("user/1"));
			Assert.assertEquals(2, redis.hdel("user/1", "first name", "last name"));
			Assert.assertEquals(0, redis.hlen("user/1"));
			//-
			Assert.assertEquals(5, redis.hincrBy("user/1", "hit", 5));
			Assert.assertEquals(20, redis.hincrBy("user/1", "hit", 15));
		}
	}
}
