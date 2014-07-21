package socket.tcp.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class TestRedisClient {
	//private final String host = "pub-redis-15190.us-east-1-3.4.ec2.garantiadata.com";
	private final String host = "localhost";
	private static final int port = 6379;

	//	private final int port = 6379;
	//private final int port = 15190;
	//	private final TcpClient tcpClient = new TcpClient("localhost", 6379);

	public static void main(String[] args) throws IOException {
		new TestRedisClient().test();
	}

	public void test() throws IOException {
		try (RedisClient redis = new RedisClient(host, port)) {
			//test.auth("kleegroup");
			redis.flushall();
			redis.lpush("actors", "marlon");
			redis.lpush("actors", "clint");
			System.out.println("echo >" + redis.echo("coucou"));
			String actor = redis.lpop("actors");
			System.out.println("lpop actors >" + actor);
			System.out.println("llen actors >" + redis.llen("actors"));
			//---
			Map<String, String> map = new HashMap<>();
			map.put("name", "john");
			map.put("lastname", "doe");
			redis.hmset("user", map);
		}
	}
}
