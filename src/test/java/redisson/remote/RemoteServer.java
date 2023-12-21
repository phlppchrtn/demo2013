package redisson.remote;

import java.io.IOException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RemoteServer {

	public static void main(final String[] args) throws IOException {
		final Config config = new Config();
		config.useSingleServer()
				.setAddress("redis-14926.c10.us-east-1-3.ec2.cloud.redislabs.com:14926")
				.setConnectionMinimumIdleSize(1)
				.setConnectionPoolSize(2);

		final RedissonClient server = Redisson.create(config);
		try {
			server.getRemoteService().register(RemoteInterface.class, new RemoteImpl());
			System.in.read();
		} finally {
			server.shutdown();
		}
	}

}
