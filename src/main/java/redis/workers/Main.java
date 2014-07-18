package redis.workers;

import kasper.work.local.WorkManagerTest.MyWorkResultHanlder;
import kasper.work.mock.DivideWork;

import org.junit.Assert;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * On crée des taches et on lances simultanément des workers.
 * 
 * @author pchretien
 */

public class Main {
	private static final String HOST = "localhost";
	private static final int WORKERS = 10;

	public static void main(final String[] args) throws Exception {
		final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxActive(20);
		final JedisPool jedisPool = new JedisPool(jedisPoolConfig, HOST);
		final ZClientWork clientWork = new ZClientWork(jedisPool);
		new Thread(clientWork).start();

		reset(jedisPool);

		startWorkers(jedisPool, WORKERS);

		while (true) {
			if ("roro".length() == 78) {
				testASync(jedisPool, clientWork);
			}
			final long start = System.currentTimeMillis();
			for (int i = 0; i < 10; i++) {
				testSync(jedisPool, clientWork, i);
				//	Thread.sleep(1000); //1s
			}
			System.out.println("duree sync : " + (System.currentTimeMillis() - start) + " ms");
		}
	}

	private static void reset(final JedisPool jedisPool) {
		final Jedis jedis = jedisPool.getResource();
		try {
			jedis.flushAll();
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	private static void testASync(final JedisPool jedisPool, final ZClientWork clientWork) throws InterruptedException {
		final long start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			final MyWorkResultHanlder<Long> workResultHanlder = new MyWorkResultHanlder<Long>();
			clientWork.schedule(new DivideWork(100, 20), workResultHanlder, 1000);
			final boolean finished = workResultHanlder.waitFinish(1100);
			Assert.assertTrue("Work non terminé ", finished);
		}
		//	waitForAll(workItems, 10);
		System.out.println("duree async: " + (System.currentTimeMillis() - start) + " ms");
	}

	private static void testSync(final JedisPool jedisPool, final ZClientWork clientWork, final int i) {
		//		final long start = System.currentTimeMillis();
		try {
			final Object res = clientWork.process(new DivideWork(100, 20), 5);
			if (i == 1) {
				System.out.println("res :" + res);
				//		System.out.println("duree sync [" + i + "] : " + (System.currentTimeMillis() - start) + " ms");
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	private static void startWorkers(final JedisPool jedisPool, final int workers) {
		for (int i = 0; i < workers; i++) {
			new Thread(new ZWorker("id:" + i, jedisPool)).start();
		}
	}

}
