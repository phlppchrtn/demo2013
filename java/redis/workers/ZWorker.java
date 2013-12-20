package redis.workers;

import kasper.kernel.util.Assertion;
import kasper.work.Work;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class ZWorker implements Runnable {
	private final String id;
	private final JedisPool jedisPool;

	ZWorker(final String id, final JedisPool jedisPool) {
		Assertion.notEmpty(id);
		Assertion.notNull(jedisPool);
		//-----------
		this.id = id;
		this.jedisPool = jedisPool;
	}

	private void execute(final Jedis jedis, final String workId) {
		final String base64 = jedis.hget("work:" + workId, "base64");
		final Work work = (Work) Util.decode(base64);
		final boolean sync = "true".equals(jedis.hget("work:" + workId, "sync"));
		//System.out.println("   - work :" + work.getClass().getSimpleName());
		try {
			final Object result = work.getEngine().process(work);
			jedis.hset("work:" + workId, "result", Util.encode(result));
			jedis.hset("work:" + workId, "status", "ok");
		} catch (final Throwable t) {
			jedis.hset("work:" + workId, "status", "ko");
			jedis.hset("work:" + workId, "error", Util.encode(t));
		}
		if (sync) {
			jedis.lpush("works:done:" + workId, workId);
		} else {
			//mettre en id de client
			jedis.lpush("works:done", workId);
		}
	}

	public void run() {
		System.out.println("started :" + id);
		while (true) {
			doRun();
		}
	}

	private void doRun() {
		final Jedis jedis = jedisPool.getResource();
		try {
			final String workId = jedis.brpoplpush("works:todo", "works:doing", 10);
			//System.out.println("todo.size : " + jedis.llen("works:todo"));
			if (workId != null) {
				//	out.println("Worker[" + id + "]executing work [" + workId + "]");
				execute(jedis, workId);
			} else {
				//out.println(" Worker [" + id + "]waiting....");
			}
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
}
