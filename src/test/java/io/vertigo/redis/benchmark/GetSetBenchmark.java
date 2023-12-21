package io.vertigo.redis.benchmark;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import io.vertigo.redis.RedisClient;
import junit.framework.Assert;
import redis.clients.jedis.Jedis;

public final class GetSetBenchmark {
	private static String HOST = "redis-14926.c10.us-east-1-3.ec2.cloud.redislabs.com";
	//private static int PORT = 6380;
	//	private static String HOST = "kasper-redis";
	private static int PORT = 14926;
	private static final int TOTAL_OPERATIONS = 500;

	//	@Before
	//	public void setUp() {
	//		new RedisServer(PORT).start();
	//	}

	@Test
	public void playJedis() {
		try (final Jedis jedis = new Jedis(HOST, PORT)) {
			jedis.connect();
			jedis.flushAll();

			final long begin = Calendar.getInstance().getTimeInMillis();

			final List<String> results = new ArrayList<>();
			for (int n = 0; n < TOTAL_OPERATIONS; n++) {
				final String key = "foo" + n;
				jedis.set(key, "bar" + n);
				results.add(jedis.get(key));
			}

			for (int n = 0; n < TOTAL_OPERATIONS; n++) {
				Assert.assertEquals("bar" + n, results.get(n));
			}

			final long elapsed = Calendar.getInstance().getTimeInMillis() - begin;
			System.out.println("Jedis : " + ((1000 * 2 * TOTAL_OPERATIONS) / elapsed) + " ops");
		}
	}

	@Test
	public void playVedis() {
		try (RedisClient redis = new RedisClient(HOST, PORT)) {
			redis.flushAll();

			final long begin = Calendar.getInstance().getTimeInMillis();

			final List<String> results = new ArrayList<>();
			for (int n = 0; n < TOTAL_OPERATIONS; n++) {
				final String key = "foo" + n;
				redis.set(key, "bar" + n);
				results.add(redis.get(key));
			}

			for (int n = 0; n < TOTAL_OPERATIONS; n++) {
				Assert.assertEquals("bar" + n, results.get(n));
			}

			final long elapsed = Calendar.getInstance().getTimeInMillis() - begin;
			System.out.println("Vertigo-redis : " + ((1000 * 2 * TOTAL_OPERATIONS) / elapsed) + " ops");
		}
	}
}
