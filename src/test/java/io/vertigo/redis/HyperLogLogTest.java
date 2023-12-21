package io.vertigo.redis;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HyperLogLogTest {
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
	public void pfadd() {
		long status = redisClient.pfadd("foo", "a");
		assertEquals(1, status);

		status = redisClient.pfadd("foo", "a");
		assertEquals(0, status);
	}

	@Test
	public void pfcount() {
		long status = redisClient.pfadd("hll", "foo", "bar", "zap");
		assertEquals(1, status);

		status = redisClient.pfadd("hll", "zap", "zap", "zap");
		assertEquals(0, status);

		status = redisClient.pfadd("hll", "foo", "bar");
		assertEquals(0, status);

		status = redisClient.pfcount("hll");
		assertEquals(3, status);
	}

	@Test
	public void pfcounts() {
		long status = redisClient.pfadd("hll_1", "foo", "bar", "zap");
		assertEquals(1, status);
		status = redisClient.pfadd("hll_2", "foo", "bar", "zap");
		assertEquals(1, status);

		status = redisClient.pfadd("hll_3", "foo", "bar", "baz");
		assertEquals(1, status);
		status = redisClient.pfcount("hll_1");
		assertEquals(3, status);
		status = redisClient.pfcount("hll_2");
		assertEquals(3, status);
		status = redisClient.pfcount("hll_3");
		assertEquals(3, status);

		status = redisClient.pfcount("hll_1", "hll_2");
		assertEquals(3, status);

		status = redisClient.pfcount("hll_1", "hll_2", "hll_3");
		assertEquals(4, status);

	}

	@Test
	public void pfmerge() {
		long status = redisClient.pfadd("hll1", "foo", "bar", "zap", "a");
		assertEquals(1, status);

		status = redisClient.pfadd("hll2", "a", "b", "c", "foo");
		assertEquals(1, status);

		redisClient.pfmerge("hll3", "hll1", "hll2");

		status = redisClient.pfcount("hll3");
		assertEquals(6, status);
	}

}
