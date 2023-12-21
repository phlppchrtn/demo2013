package redisson.executor;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.RExecutorService;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;

public class ExecutorServiceExamples {

	public static void main(final String[] args) throws InterruptedException, ExecutionException {
		final Config config = new Config();
		config.useSingleServer()
				.setAddress("redis-14926.c10.us-east-1-3.ec2.cloud.redislabs.com:14926")
				.setConnectionMinimumIdleSize(1)
				.setConnectionPoolSize(2);

		final RedissonNodeConfig nodeConfig = new RedissonNodeConfig(config);
		nodeConfig.setExecutorServiceWorkers(Collections.singletonMap("myExecutor", 1));
		final RedissonNode node = RedissonNode.create(nodeConfig);
		node.start();

		final RedissonClient client = Redisson.create(config);
		final RExecutorService e = client.getExecutorService("myExecutor");
		System.out.println("executorService.isShutdown :" + e.isShutdown());
		System.out.println("executorService.isTerminated :" + e.isTerminated());
		//e.execute(new RunnableTask());
		final Future<String> result = e.submit(new CallableTask());
		System.out.println("result =" + result.get());
		e.shutdown();

		node.shutdown();
	}

}
