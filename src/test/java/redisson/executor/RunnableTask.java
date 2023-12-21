package redisson.executor;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.RInject;

public class RunnableTask implements Runnable {

	@RInject
	RedissonClient redisson;

	@Override
	public void run() {
		final RMap<String, String> map = redisson.getMap("myMap");
		map.put("5", "11");
	}

}
