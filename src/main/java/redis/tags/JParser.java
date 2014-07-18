package redis.tags;

import java.io.File;
import java.io.IOException;

public class JParser {
	private int javaFiles;
	private final JHandler jHandler;

	JParser(JHandler jHandler) {
		this.jHandler = jHandler;
	}

	public void parseDirectory(File directory) throws IOException {

		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				parseDirectory(file);
			} else {
				parseFile(file);
			}
		}
	}

	private void parseFile(File file) throws IOException {
		if (file.getName().endsWith(".java")) {
			parseJavaFile(file);
		}
	}

	private void parseJavaFile(File file) throws IOException {
		javaFiles++;
		if (javaFiles % 100 == 0) {
			System.out.println("Nomnbre de docs :" + javaFiles);
		}

		jHandler.onJavaFile(file);
	}
}
