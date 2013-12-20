package redis.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mxp.lucene.store.RedisDirectory;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

public class Indexa {
	//	static Analyzer ANALYZER = new StandardAnalyzer(Version.LUCENE_36);
	static Analyzer ANALYZER = new SimpleAnalyzer(Version.LUCENE_36);

	/**
	 * @param args
	 * @throws IOException
	 * @throws CorruptIndexException
	 * @throws ParseException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws CorruptIndexException, IOException, ParseException, InterruptedException {
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		JedisShardInfo si = new JedisShardInfo("localhost", 6379);
		//		JedisShardInfo si2 = new JedisShardInfo("localhost", 6389);
		//		JedisShardInfo si3 = new JedisShardInfo("localhost", 6399);
		shards.add(si);
		//		shards.add(si2);
		//		shards.add(si3);
		ShardedJedisPool pool = new ShardedJedisPool(new GenericObjectPool.Config(), shards);
		try {
			//	pool.destroy();
			RedisDirectory directory = new RedisDirectory("piratebay", pool);
			//	pool.
			//	FSDirectory fsDir = FSDirectory.open(new File("/Users/maxpert/labs/pbindex"));

			doDump(directory);

			doSearch(directory);
		} finally {
			pool.destroy();
		}
	}

	private static void doSearch(RedisDirectory directory) throws ParseException, CorruptIndexException, IOException {
		//Test searching ;)
		search(directory, "content", "kasper");
		search(directory, "content", "publisher");
		search(directory, "content", "pchretien");
		search(directory, "content", "npiedeloup");
	}

	private static void doDump(RedisDirectory directory) throws IOException, InterruptedException {
		long start = System.currentTimeMillis();
		System.out.println("Indexing in Redis...");
		DumpKasper(directory);
		System.out.printf("Redis indexing took %d ms", System.currentTimeMillis() - start);
		System.out.println();
		Thread.sleep(5000);
	}

	private static void parseDirectory(File dir, DumpIndexWriter writer) throws IOException {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				parseDirectory(file, writer);
			} else {
				parseFile(file, writer);
			}
		}
	}

	private static void parseFile(File file, DumpIndexWriter writer) throws IOException {
		if (file.getName().endsWith(".java")) {
			writer.parseJavaFile(file);
		}
	}

	private static void DumpKasper(Directory directory) throws IOException {
		File kasperDirectory = new File("D:cvs/kasper/kasper6");
		DumpIndexWriter writer = new DumpIndexWriter(directory);
		try {
			parseDirectory(kasperDirectory, writer);
		} finally {
			writer.close();
		}
	}

	private static void search(Directory index, String field, String query) throws ParseException, CorruptIndexException, IOException {
		//int hitsPerPage = 10;
		QueryParser qp = new QueryParser(Version.LUCENE_36, field, ANALYZER);
		Query q = qp.parse(query);
		IndexReader reader = IndexReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		//TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		TopDocs topdocs = searcher.search(q, 10);

		//searcher.search(q, collector);
		//ScoreDoc[] hits = collector.topDocs().scoreDocs;

		System.out.println("query [" + query + "]  : " + topdocs.totalHits + " hits.");
		for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
			//			scoreDoc.doc	
			//	int docId = hits[i].doc;
			Document d = searcher.doc(scoreDoc.doc);
			System.out.println(" - " + d.get("title"));
		}
		searcher.close();
	}
}
