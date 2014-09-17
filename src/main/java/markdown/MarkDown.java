package markdown;

import io.vertigo.core.lang.Assertion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MarkDown {
	private final File srcDirectory;
	private final Site site;

	public MarkDown(final File srcDirectory, final File targetDirectory) {
		Assertion.checkNotNull(srcDirectory);
		Assertion.checkArgument(srcDirectory.isDirectory(), "A directory file is required");
		//---------------------------------------------------------------------
		this.srcDirectory = srcDirectory;
		//1. Cr�ation d'un environnement transverse
		site = new Site(srcDirectory, targetDirectory);
	}

	public void generate() throws Exception {

		//1. Lecture de toutes les notes 
		for (File file : srcDirectory.listFiles()) {
			if (file.getCanonicalPath().endsWith(".md")) {
				Note note = createNote(file);
				site.add(note);
			}
		}

		//2. G�n�ration du site
		site.build();
	}

	private static Note createNote(File file) throws IOException {
		final StringBuilder markdown = new StringBuilder();
		Note note = new Note(file, markdown);

		try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(note.getSrcFile()))) {
			boolean meta = true;
			String strLine;
			while ((strLine = bufferedReader.readLine()) != null) {
				if (meta && strLine.startsWith("@")) {
					extractMetas(strLine, note.getDatas());
				} else {
					// D�s qu'il n'y a plus de ligne commen�ant par @ alors on
					// passe en markdown pur.
					meta = false;
					note.getMarkdown().append(strLine);
					note.getMarkdown().append("\r\n");
				}

			}
		}
		return note;
	}

	private static Map<String, Object> extractMetas(final String strLine, final Map<String, Object> data) {
		Assertion.checkNotNull(strLine);
		Assertion.checkArgument(strLine.startsWith("@"), "metadata line must start with @");
		//---------------------------------------------------------------------
		int idx = strLine.indexOf(' ');
		final String meta;
		//Nom de la m�ta en enlevant le @ du d�but.
		if (idx > 0) {
			meta = strLine.substring(1, idx);
		} else {
			meta = strLine.substring(1);
		}

		List<String> list = new ArrayList<>();
		while (idx > 0) {
			int sep = strLine.indexOf('"', idx + 1);
			int white = strLine.indexOf(' ', idx + 1);
			if (sep > 0 && (white < 0 || sep < white)) {
				//On est dans le cas d'un groupe de mots entour�s par des "  "
				idx = sep;
				sep = strLine.indexOf('"', sep + 1);
				if (sep < 0) {
					throw new IllegalArgumentException("expected \" on line " + strLine);
				}
				list.add(strLine.substring(idx + 1, sep));
				idx = sep;
			} else if (white > 0) {
				String word = strLine.substring(idx + 1, white).trim();
				//On skippe les blancs 
				if (!word.isEmpty()) {
					list.add(word);
				}
				idx = white;
			} else {
				String word = strLine.substring(idx + 1, strLine.length()).trim();
				//On skippe les blancs 
				if (!word.isEmpty()) {
					list.add(word);
				}
				break;
			}
		}

		if (meta.endsWith("s")) {
			//On a une liste
			data.put(meta, list);
		} else {
			data.put(meta, list.get(0));
		}
		return data;
	}

}
