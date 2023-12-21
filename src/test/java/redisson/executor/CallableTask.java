package redisson.executor;

import java.util.concurrent.Callable;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.RInject;

public class CallableTask implements Callable<String> {

	@RInject
	RedissonClient redisson;

	@Override
	public String call() throws Exception {
		final RMap<String, String> map = redisson.getMap("myMap");
		map.put("1", "2");
		//return map.get("3");
		return "3";
	}

}
