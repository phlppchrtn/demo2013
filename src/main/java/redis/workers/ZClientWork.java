package redis.workers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.Assertion;
import kasper.kernel.util.DateUtil;
import kasper.work.Work;
import kasper.work.WorkResultHandler;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class ZClientWork implements Runnable {
	private final JedisPool jedisPool;
	private final Map<String, WorkResultHandler> workResultHandlers = Collections.synchronizedMap(new HashMap<String, WorkResultHandler>());

	ZClientWork(final JedisPool jedisPool) {
		Assertion.notNull(jedisPool);
		//-----------
		this.jedisPool = jedisPool;
	}

	public <WR, W extends Work<WR, W>> void schedule(final W work, final WorkResultHandler<WR> workResultHandler, final int timeoutSeconds) {
		final Jedis jedis = jedisPool.getResource();
		try {
			doSchedule(jedis, work, workResultHandler, timeoutSeconds);
		} catch (final Exception e) {
			jedisPool.returnBrokenResource(jedis);
			throw new KRuntimeException(e);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	private <WR, W extends Work<WR, W>> WorkResultHandler<WR> doSchedule(final Jedis jedis, final W work, final WorkResultHandler<WR> workResultHandler, final int timeoutSeconds) {
		final String workId = publish(jedis, work, false);
		workResultHandlers.put(workId, workResultHandler);
		return workResultHandler;
	}

	public Object process(final Work work, final int timeoutSeconds) {
		final Jedis jedis = jedisPool.getResource();
		try {
			return doProcess(jedis, work, timeoutSeconds);
		} catch (final Exception e) {
			jedisPool.returnBrokenResource(jedis);
			throw new KRuntimeException(e);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	private Object doProcess(final Jedis jedis, final Work work, final int timeoutSeconds) {
		//On renseigne la demande de travaux
		final String id = publish(jedis, work, true);

		//On attend le résultat
		final String workId = jedis.brpoplpush("works:done:" + id, "works:completed", timeoutSeconds);
		if (workId == null) {
			throw new KRuntimeException("TimeOut survenu pour {0}, durée maximale: {1}", null, id, timeoutSeconds);
		}
		if (!workId.equals(id)) {
			throw new IllegalStateException("Id non cohérenents attendu '" + id + "' trouvé '" + workId + "'");
		}
		if ("ok".equals(jedis.hget("work:" + id, "status"))) {
			return Util.decode(jedis.hget("work:" + id, "result"));
		}
		final Throwable t = (Throwable) Util.decode(jedis.hget("work:" + id, "error"));
		if (t instanceof RuntimeException) {
			return t;
		}
		throw new KRuntimeException(t);
	}

	private String publish(final Jedis jedis, final Work work, final boolean sync) {
		final UUID uuid = UUID.randomUUID();
		final String workId = uuid.toString();

		//out.println("creating work [" + workId + "] : " + work.getClass().getSimpleName());
		jedis.hset("work:" + workId, "base64", Util.encode(work));
		jedis.hset("work:" + workId, "date", DateUtil.newDate().toString());
		jedis.hset("work:" + workId, "sync", Boolean.toString(sync));
		//On publie la demande de travaux
		jedis.lpush("works:todo", workId);
		return workId;
	}

	public void run() {
		final Jedis jedis = jedisPool.getResource();
		try {
			while (true) {
				//On attend le résultat
				final String workId = jedis.brpoplpush("works:done", "works:completed", 60);
				if (workId != null) {
					final Object result = Util.decode(jedis.hget("work:" + workId, "result"));
					workResultHandlers.get(workId).onSuccess(result);
				}
			}
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
}
