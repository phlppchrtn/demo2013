package redis.test;

import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;

public final class JRedisDataBase {
	private final Jedis jedis;

	public JRedisDataBase() {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
		jedis = pool.getResource();
	}

	void reset() {
		jedis.flushDB();
	}

	Set<String> findItemsByTag(String tag) {
		return jedis.sinter("tags:" + tag);
	}

	void tagItem(int itemId, String... tags) {
		for (String tag : tags) {
			jedis.sadd("tags:" + tag, "items:" + itemId);
		}
	}

	//	void userTagItem(String userId, int itemId, String... tags) {
	//		for (String tag : tags) {
	//			jedis.zincrby("items:" + itemId, tag);
	//		}
	//	}

	void addUser(String userId) {
		jedis.lpush("users", userId);
	}

	List<String> getUsers(int top) {
		return jedis.lrange("users", 0, top);
	}

	//-------------------------------------------------------------------------	

	void rateItem(String userId, String itemId, int rate) {
		//		Long result = jedis.sadd("items.rate:" + userId, itemId);

		jedis.zadd("rates/" + userId, rate, "item:" + itemId);
		jedis.zincrby("rates", rate, "item:" + itemId);
	}

	Set<Tuple> findItemsByRate() {
		//	System.out.println("rates = " + jedis.zrange("rates", 0, -1));
		return jedis.zrangeWithScores("rates", 0, -1);
	}

	void addItem(String itemId, String desc) {
		jedis.sadd("items", itemId);
		jedis.hset("item:" + itemId, "desc", desc);
	}

	void addFavorite(String userId, String itemId) {
		if (jedis.sismember("items", itemId)) {
			//jedis.incrBy("favorites:" + userId + ":" + itemId, 1);
			Long result = jedis.sadd("items.favitems:" + userId, itemId);
			if (result == 1) {
				jedis.hincrBy("item:" + itemId, "fav", 1);
			}
		}
		//	jedis.incr("fav:" + itemId);

		//		jedis.hincrBy("item:" + itemId, "userfav:" + userId, 1);

		//		jedis.lpush("favorites:" + userId, itemId);
		//		jedis.zincrby("favorites", 1, itemId);
	}

	//	List<String> getLastFavorites(String userId, int limit) {
	//		return jedis.lrange("favorites:" + userId, 0, limit);
	//	}
	//	List<String> getBestFavorites(int limit) {
	//		
	//	}
	List<String> getBestFavorites(int limit) {
		SortingParams sortingParams = new SortingParams().by("item:*->fav").get("item:*->desc");
		return jedis.sort("items", sortingParams);
		//.jedis.incrBy("favorites:" + userId + ":" + itemId, 1);

		//return jedis.zrangeWithScores("favorites", 0, limit);
	}
	//-------------------------------------------------------------------------	

	//	void addRate(String userId, String itemId, int rate) {
	//		jedis.zadd("favorites:" + userId, itemId);
	//	}

}
