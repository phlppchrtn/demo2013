package markdown;

import java.io.File;

public class Main {
	public static void main(final String[] args) throws Exception {
		File srcFile = new File("F:/markdown/src");
		File targetFile = new File("F:/markdown/target");
		final MarkDown markDown = new MarkDown(srcFile, targetFile);
		markDown.generate();
	}
}
