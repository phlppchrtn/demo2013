package redis.test;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Tuple;

public class JRedisTest {
	private final JRedisDataBase jdb = new JRedisDataBase();

	@Before
	public void setUp() {
		jdb.reset();
	}

	@Test
	//Vérification basique du fonctionnement de la liste
	public void users() {
		jdb.addUser("pch");
		jdb.addUser("npi");
		jdb.addUser("jpe");
		jdb.addUser("lba");
		final List<String> users = jdb.getUsers(-1);
		Assert.assertEquals(5, users.size());
		Assert.assertTrue(users.contains("pch"));
		Assert.assertTrue(users.contains("npi"));
		Assert.assertTrue(users.contains("jpe"));
		Assert.assertTrue(users.contains("lba"));
	}

	@Test
	//Vérification le la non unicité des données entrées dans une liste
	public void users2() {
		jdb.addUser("pch");
		jdb.addUser("pch");
		final List<String> users = jdb.getUsers(-1);
		Assert.assertEquals(2, users.size());
		Assert.assertTrue(users.contains("pch"));
	}

	@Test
	public void favorites() {
		jdb.addItem("beatles", "TheBeatles");
		jdb.addItem("bowie", "David bowie ");
		jdb.addItem("dalida", "Dalida");

		jdb.addFavorite("pch", "beatles");
		jdb.addFavorite("pch", "bowie");
		jdb.addFavorite("pch", "bowie");
		jdb.addFavorite("pch", "bowie");
		jdb.addFavorite("pch", "bowie");
		jdb.addFavorite("pch", "bowie");
		jdb.addFavorite("jpe", "dalida");
		jdb.addFavorite("lch", "beatles");
		jdb.addFavorite("npi", "beatles");
		jdb.addFavorite("npi", "dalida");

		final List<String> bestFavorites = jdb.getBestFavorites(-1);
		Assert.assertEquals(3, bestFavorites.size());

		System.out.print("favorites >>");
		System.out.println(bestFavorites);
		//		Set<Tuple> bestFavorites = jdb.getBestFavorites(-1);
		//		Assert.assertEquals(3, bestFavorites.size());
		//
		//		printTuples(bestFavorites);
	}

	@Test
	public void tags() {
		jdb.tagItem(1, "cubisme", "XXE", "bleu", "rose"); //picasso
		jdb.tagItem(2, "cubisme", "XXE");//braque
		jdb.tagItem(3, "fauvisme"); //matisse
		System.out.println("tag=cubisme >>" + jdb.findItemsByTag("cubisme"));
	}

	@Test
	public void rates() {
		//		int produit1 = 1;
		//		int produit2 = 2;
		//		int produit3 = 3;
		//		int produit4 = 4;

		jdb.rateItem("pch", "p1", 2);
		jdb.rateItem("npi", "p1", 1);

		jdb.rateItem("pch", "p2", 1);
		jdb.rateItem("npi", "p2", 2);
		jdb.rateItem("lba", "p2", 1);

		jdb.rateItem("pch", "p3", 1);
		jdb.rateItem("npi", "p3", 2);
		jdb.rateItem("lba", "p3", 1);
		jdb.rateItem("jpe", "p3", 3);

		jdb.rateItem("jpe", "p4", 1);

		System.out.print(">>>rates : ");
		printTuples(jdb.findItemsByRate());
	}

	private static void printTuples(final Set<Tuple> tuples) {
		final StringBuilder buff = new StringBuilder("[ ");
		for (final Tuple tuple : tuples) {
			buff.append(tuple.getElement() + " ==> " + tuple.getScore() + ", ");
		}
		System.out.println(buff.toString());
	}
}
