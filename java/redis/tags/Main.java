package redis.tags;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * On indexe des fichiers Java à partir d'un répertioire et on récupère leusr auteurs.
 * scard author:XXXX => donne le nombre de fichiers de cet auteur
 * scard author:anonymous => donne le nombre de fichiers où l'auteur n'est pas renseigné
 * sinter author:XXX author:YYYY donne la liste des fichiers ou les auteurs ont participé
 * smembers author:XXX est identique à sinter author:XXX
 * smembers java:XXX  donne la liste des auteurs de ce fichiers
 * zrange zauthors 0 -1 withscores donne la liste des auteurs avec leurs scores
 * zrevrange 0 5 withscores donne la liste des 5 plus grow contributeurs
 * @author pchretien
 */

public class Main {
	public static void main(final String[] args) throws IOException {
		final JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

		parse("D:cvs/kasper/kasper6", pool);
	}

	private static void parse(final String directory, final JedisPool pool) throws IOException {
		final JHandler jhandler = new MyJHandler(pool);
		final JParser jparser = new JParser(jhandler);
		jparser.parseDirectory(new File(directory));
	}

	private static class MyJHandler implements JHandler {
		private final JedisPool pool;

		MyJHandler(final JedisPool pool) {
			this.pool = pool;
		}

		public void onJavaFile(final File file) throws IOException {
			final Jedis jedis = pool.getResource();
			try {
				jedis.lpush("java", file.getName());
				final List<String> authors = extractAuthors(file);
				if (authors.isEmpty()) {
					authors.add("anonymous");
				}
				for (final String author : authors) {
					System.out.println(">> " + file.getName() + "==>" + author);
					jedis.sadd("java:" + file.getName(), author);
					final Long added = jedis.sadd("author:" + author, file.getName());
					//On ajoute à la liste des auteurs
					jedis.sadd("authors", author);
					//la variable added permet de n'ajouter l'auteur que si il n'a pas déja été.
					jedis.zincrby("zauthors", added.doubleValue(), author);
				}

			} finally {
				pool.returnResource(jedis);
			}
		}

		private static List<String> extractAuthors(final File file) throws IOException {
			final List<String> authors = new ArrayList<String>();
			final BufferedReader br = new BufferedReader(new FileReader(file));
			try {
				String strLine;
				while ((strLine = br.readLine()) != null) {
					final int idx = strLine.indexOf("@author");
					if (idx > 0) {
						strLine = strLine.substring(idx + "@author".length());
						final String[] tmpAuthors = strLine.split(",");
						for (final String tmpAuthor : tmpAuthors) {
							authors.add(tmpAuthor.trim());
						}
					}
				}
			} finally {
				br.close();
			}
			return authors;
		}
	}
}
