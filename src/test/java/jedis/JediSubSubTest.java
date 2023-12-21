package jedis;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class JediSubSubTest {
	private static String HOST = "redis-14926.c10.us-east-1-3.ec2.cloud.redislabs.com";
	//private static int PORT = 6380;
	//	private static String HOST = "kasper-redis";
	private static int PORT = 14926;

	@Test
	public void playJedis() {
		final JedisPubSub jedisPubSub = new JedisPubSub() {
			@Override
			public void onPMessage(final String pattern, final String channel, final String message) {
				System.out.println(">>=>" + message);
			}

			@Override
			public void onMessage(final String channel, final String message) {
				System.out.println(">>channel:" + channel + "  =>" + message);
			}
		};

		try (final Jedis jedis = new Jedis(HOST, PORT)) {
			//			jedis.subscribe(jedisPubSub, "v/news", "v/meteo");
			jedis.psubscribe(jedisPubSub, "v/*");
			//jedis est bloquant sur les méthodes suscribe.. on ne peut en appeler qu'une seule.
		}
		System.out.println("end---------------");
	}
}
