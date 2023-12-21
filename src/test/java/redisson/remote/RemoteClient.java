package redisson.remote;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RemoteClient {

	public static void main(final String[] args) {
		final Config config = new Config();
		config.useSingleServer()
				.setAddress("redis-14926.c10.us-east-1-3.ec2.cloud.redislabs.com:14926")
				.setConnectionMinimumIdleSize(1)
				.setConnectionPoolSize(2);

		final RedissonClient client = Redisson.create(config);
		try {

			final RemoteInterface service = client.getRemoteService().get(RemoteInterface.class);

			final long result = service.myMethod(21L);
			System.out.println("result = " + result);

		} finally {
			client.shutdown();
		}
	}

}
