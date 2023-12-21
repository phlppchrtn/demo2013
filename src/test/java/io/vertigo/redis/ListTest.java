package io.vertigo.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.vertigo.redis.RedisClient;

public class ListTest {
	private RedisClient redisClient;

	private static RedisClient createClient() {
		return new RedisClient("redis-14926.c10.us-east-1-3.ec2.cloud.redislabs.com", 14926);
	}

	@Before
	public void setUp() {
		//		final RedisServer redisServer = new RedisServer(6379);
		//		redisServer.start();
		//----
		redisClient = createClient();
		redisClient.flushAll();
	}

	@After
	public void doTearDown() {
		if (redisClient != null) {
			redisClient.close();
		}
	}

	@Test
	public void rpush() {
		long size = redisClient.rpush("foo", "bar");
		assertEquals(1, size);
		size = redisClient.rpush("foo", "foo");
		assertEquals(2, size);
		size = redisClient.rpush("foo", "bar", "foo");
		assertEquals(4, size);
	}

	@Test
	public void lpush() {
		long size = redisClient.lpush("foo", "bar");
		assertEquals(1, size);
		size = redisClient.lpush("foo", "foo");
		assertEquals(2, size);
		size = redisClient.lpush("foo", "bar", "foo");
		assertEquals(4, size);
	}

	@Test
	public void llen() {
		assertEquals(0, redisClient.llen("foo"));
		redisClient.lpush("foo", "bar");
		redisClient.lpush("foo", "car");
		assertEquals(2, redisClient.llen("foo"));
	}

	@Test(expected = Exception.class)
	public void llenNotOnList() {
		redisClient.set("foo", "bar");
		redisClient.llen("foo");
		fail("llen must be called on a list");
	}

	@Test
	public void lrange() {
		redisClient.rpush("foo", "a");
		redisClient.rpush("foo", "b");
		redisClient.rpush("foo", "c");

		List<String> expected = Arrays.asList("a", "b", "c");

		List<String> range = redisClient.lrange("foo", 0, 2);
		assertEquals(expected, range);

		range = redisClient.lrange("foo", 0, 20);
		assertEquals(expected, range);

		expected = Arrays.asList("b", "c");

		range = redisClient.lrange("foo", 1, 2);
		assertEquals(expected, range);

		expected = new ArrayList<>();
		range = redisClient.lrange("foo", 2, 1);
		assertEquals(expected, range);
	}

	@Test
	public void ltrim() {
		redisClient.lpush("foo", "1");
		redisClient.lpush("foo", "2");
		redisClient.lpush("foo", "3");
		redisClient.ltrim("foo", 0, 1);

		final List<String> expected = Arrays.asList("3", "2");

		assertEquals(2, redisClient.llen("foo"));
		assertEquals(expected, redisClient.lrange("foo", 0, 100));
	}

	@Test
	public void lindex() {
		redisClient.lpush("foo", "1");
		redisClient.lpush("foo", "2");
		redisClient.lpush("foo", "3");

		final List<String> expected = Arrays.asList("3", "bar", "1");

		redisClient.lset("foo", 1, "bar");

		assertEquals(expected, redisClient.lrange("foo", 0, 100));
	}

	@Test
	public void lset() {
		redisClient.lpush("foo", "1");
		redisClient.lpush("foo", "2");
		redisClient.lpush("foo", "3");

		assertEquals("3", redisClient.lindex("foo", 0));
		assertEquals(null, redisClient.lindex("foo", 100));
	}

	@Test
	public void lrem() {
		redisClient.lpush("foo", "hello");
		redisClient.lpush("foo", "hello");
		redisClient.lpush("foo", "x");
		redisClient.lpush("foo", "hello");
		redisClient.lpush("foo", "c");
		redisClient.lpush("foo", "b");
		redisClient.lpush("foo", "a");

		final long count = redisClient.lrem("foo", -2, "hello");

		final List<String> expected = Arrays.asList("a", "b", "c", "hello", "x");

		assertEquals(2, count);
		assertEquals(expected, redisClient.lrange("foo", 0, 1000));
		assertEquals(0, redisClient.lrem("bar", 100, "foo"));
	}

	@Test
	public void lpop() {
		redisClient.rpush("foo", "a");
		redisClient.rpush("foo", "b");
		redisClient.rpush("foo", "c");

		String element = redisClient.lpop("foo");
		assertEquals("a", element);

		final List<String> expected = Arrays.asList("b", "c");

		assertEquals(expected, redisClient.lrange("foo", 0, 1000));
		redisClient.lpop("foo");
		redisClient.lpop("foo");

		element = redisClient.lpop("foo");
		assertEquals(null, element);
	}

	@Test
	public void rpop() {
		redisClient.rpush("foo", "a");
		redisClient.rpush("foo", "b");
		redisClient.rpush("foo", "c");

		String element = redisClient.rpop("foo");
		assertEquals("c", element);

		final List<String> expected = Arrays.asList("a", "b");

		assertEquals(expected, redisClient.lrange("foo", 0, 1000));
		redisClient.rpop("foo");
		redisClient.rpop("foo");

		element = redisClient.rpop("foo");
		assertEquals(null, element);
	}

	@Test
	public void rpoplpush() {
		redisClient.rpush("foo", "a");
		redisClient.rpush("foo", "b");
		redisClient.rpush("foo", "c");

		redisClient.rpush("dst", "foo");
		redisClient.rpush("dst", "bar");

		final String element = redisClient.rpoplpush("foo", "dst");

		assertEquals("c", element);

		final List<String> srcExpected = Arrays.asList("a", "b");

		final List<String> dstExpected = Arrays.asList("c", "foo", "bar");

		assertEquals(srcExpected, redisClient.lrange("foo", 0, 1000));
		assertEquals(dstExpected, redisClient.lrange("dst", 0, 1000));
	}

	@Test
	public void blpop() {
		List<String> result = redisClient.blpop(1, "foo");
		assertTrue(result.isEmpty());

		redisClient.lpush("foo", "bar");
		result = redisClient.blpop(1, "foo");

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("foo", result.get(0));
		assertEquals("bar", result.get(1));
	}

	@Test
	public void brpop() {
		List<String> result = redisClient.brpop(1, "foo");
		assertTrue(result.isEmpty());

		redisClient.lpush("foo", "bar");
		result = redisClient.brpop(1, "foo");
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("foo", result.get(0));
		assertEquals("bar", result.get(1));
	}

	@Test
	public void lpushx() {
		long status = redisClient.lpushx("foo", "bar");
		assertEquals(0, status);

		redisClient.lpush("foo", "a");
		status = redisClient.lpushx("foo", "b");
		assertEquals(2, status);
	}

	@Test
	public void rpushx() {
		long status = redisClient.rpushx("foo", "bar");
		assertEquals(0, status);

		redisClient.lpush("foo", "a");
		status = redisClient.rpushx("foo", "b");
		assertEquals(2, status);
	}

	@Test
	public void linsert() {
		long status = redisClient.linsert("foo", RedisClient.Position.BEFORE, "bar", "car");
		assertEquals(0, status);

		redisClient.lpush("foo", "a");
		status = redisClient.linsert("foo", RedisClient.Position.AFTER, "a", "b");
		assertEquals(2, status);

		final List<String> actual = redisClient.lrange("foo", 0, 100);
		final List<String> expected = Arrays.asList("a", "b");

		assertEquals(expected, actual);

		status = redisClient.linsert("foo", RedisClient.Position.BEFORE, "bar", "car");
		assertEquals(-1, status);
	}

	@Test
	public void brpoplpush() {
		(new Thread(() -> {
			try {
				Thread.sleep(100);
				final RedisClient r = createClient();
				r.lpush("foo", "a");
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		})).start();

		final String element = redisClient.brpoplpush("foo", "bar", 0);

		assertEquals("a", element);
		assertEquals(1, redisClient.llen("bar"));
		assertEquals("a", redisClient.lrange("bar", 0, -1).get(0));
	}

}
