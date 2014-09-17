package markdown;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.vertigo.core.lang.Assertion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.petebevin.markdown.MarkdownProcessor;

public final class Site {
	private final File srcDirectory;
	private final File targetDirectory;

	private final List<Note> notes = new ArrayList<>();
	private final Set<String> siteCategories = new HashSet<>();

	public Site(final File srcDirectory, final File targetDirectory) {
		Assertion.checkNotNull(srcDirectory);
		Assertion.checkArgument(srcDirectory.isDirectory(), "A directory file is required");
		Assertion.checkNotNull(targetDirectory);
		Assertion.checkArgument(targetDirectory.isDirectory(), "A target directory file is required");
		//---------------------------------------------------------------------
		this.srcDirectory = srcDirectory;
		this.targetDirectory = targetDirectory;
	}

	void build() throws Exception {
		siteCategories.clear();

		StringBuilder locations = new StringBuilder("[ ");
		for (Note note : notes) {
			String location = (String) note.getDatas().get("ll");
			if (location != null) {
				String[] tokens = location.split(",");
				if (locations.length() > 2) {
					locations.append(", ");
				}
				locations.append("{\"lat\":\"" + tokens[0] + "\", \"lng\":\"" + tokens[1] + "\"}");
			}
		}
		locations.append("]");

		//---------------------------------------------------------------------
		for (Note note : notes) {
			List<String> categories = note.getCategories();
			if (!categories.isEmpty()) {
				siteCategories.add(categories.get(0));
			}
		}

		// G�n�ration des pages HTML 
		generate();
		generateMap(locations.toString());
	}

	public Set<String> getCategories() {
		return siteCategories;
	}

	public void add(Note note) {
		Assertion.checkNotNull(note);
		//---------------------------------------------------------------------s
		notes.add(note);
	}

	public List<Note> getNotes() {
		return notes;
	}

	private void generateMap(String jsonLocations) throws Exception {
		//---------------------------------------------------------------------
		//final MarkdownProcessor markdownProcessor = new MarkdownProcessor();
		Template template = generateWithTemplate("map.ftl");
		String fileName = targetDirectory.getCanonicalPath() + "/locations.html";
		try (Writer writer = new FileWriter(new File(fileName))) {
			Map<String, Object> map = new HashMap<>();
			//Gson gson = new GsonBuilder().create();
			map.put("locations", jsonLocations);
			//				map.put("content", markdownProcessor.markdown(note.getMarkdown().toString()));
			//				map.put("site", this);
			template.process(map, writer);
			writer.flush();
		}
	}

	private File createTargetFile(File srcFile) throws IOException {
		//On the left 
		// -replacing srcDirectory by targetDirectory
		//On the right 
		// -replacing .md by .html
		String name = srcFile.getCanonicalPath();
		name = name.substring(srcDirectory.getCanonicalPath().length(), name.length());
		name = name.substring(0, name.length() - ".md".length()) + ".html";
		name = targetDirectory.getCanonicalPath() + name;
		return new File(name);
	}

	private void generate() throws Exception {
		//---------------------------------------------------------------------
		final MarkdownProcessor markdownProcessor = new MarkdownProcessor();

		for (Note note : notes) {
			Template template = generateWithTemplate("template.ftl");
			try (Writer writer = new FileWriter(createTargetFile(note.getSrcFile()))) {
				Map<String, Object> map = new HashMap<>();
				map.put("note", note);
				map.put("content", markdownProcessor.markdown(note.getMarkdown().toString()));
				map.put("site", this);
				template.process(map, writer);
			}
		}
	}

	private Template generateWithTemplate(String templateFileName) throws IOException, URISyntaxException {
		// Freemarker configuration object
		Configuration configuration = new Configuration();
		configuration.setDirectoryForTemplateLoading(new File(MarkDown.class.getResource("/templates").toURI()));
		Template template = configuration.getTemplate(templateFileName);
		return template;
	}
}
