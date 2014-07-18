package markdown;

import io.vertigo.kernel.lang.Assertion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Note {
	private final File srcFile;
	private final Map<String, Object> datas;
	private final StringBuilder markdown;

	Note(File srcFile, StringBuilder markdown) {
		Assertion.checkNotNull(srcFile);
		Assertion.checkNotNull(markdown);
		//---------------------------------------------------------------------
		this.srcFile = srcFile;
		this.datas = new HashMap<>();
		this.markdown = markdown;
	}

	public Map<String, Object> getDatas() {
		return datas;
	}

	public StringBuilder getMarkdown() {
		return markdown;
	}

	public File getSrcFile() {
		return srcFile;
	}

	public List<String> getCategories() {
		List<String> categories = new ArrayList<>();
		String category = (String) this.getDatas().get("category");
		Assertion.checkArgument(category.startsWith("//"), "category must start with //");
		String[] tokens = category.substring(2).split("/");
		for (String token : tokens) {
			if (!token.trim().isEmpty()) {
				categories.add(token);
			}
		}
		return categories;
	}
}
