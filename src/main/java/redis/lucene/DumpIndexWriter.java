package redis.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public final class DumpIndexWriter {
	private int javaFiles;
	private final IndexWriter writer;

	public DumpIndexWriter(Directory directory) throws IOException {
		Analyzer analyzer = Indexa.ANALYZER;
		IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_36, analyzer);
		writer = new IndexWriter(directory, writerConfig);
	}

	boolean parseJavaFile(File file) throws IOException {
		javaFiles++;
		if (javaFiles % 100 == 0) {
			System.out.println("Nomnbre de docs :" + javaFiles);
		}

		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder buffer = new StringBuilder();
		String strLine;
		while ((strLine = br.readLine()) != null) {
			buffer.append(strLine).append(" ");
		}
		br.close();

		Document document = new Document();

		//		System.out.printf("%s -> %s \n", fields[1], fields[5]);
		document.add(new Field("title", file.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		document.add(new Field("content", buffer.toString(), Field.Store.NO, Field.Index.ANALYZED));

		try {
			writer.addDocument(document);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void close() throws IOException {
		writer.close();
	}
}
