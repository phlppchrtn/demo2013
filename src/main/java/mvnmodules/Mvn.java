package mvnmodules;

import io.vertigo.util.StringUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class Mvn {

	public static void main(final String[] args) throws Exception {
		//		final String url = "http://central.maven.org/maven2/io/vertigo/vertigo-dynamo-impl/0.8.2/vertigo-dynamo-impl-0.8.2.pom";
		final String url = "http://central.maven.org/maven2/org/xwiki/commons/xwiki-commons-component-api/7.2-milestone-3/xwiki-commons-component-api-7.2-milestone-3.pom";
		final MvnProject project = parse(url);

		System.out.println("|------------------------------- ");
		System.out.println("|--- " + project.getName());
		System.out.println("|------------------------------- ");
		System.out.println("|");
		display("", project);
	}

	private static String tocamelCase(final String input) {
		final String[] tokens = input.split("-");
		for (int i = 1; i < tokens.length; i++) {
			tokens[i] = StringUtil.first2UpperCase(tokens[i]);
		}
		String res = "";
		for (final String token : tokens) {
			res += token;
		}
		return res;
	}

	private static PrintStream offset(final String offset) {
		System.out.print(offset.isEmpty() ? "" : "|" + offset);
		return System.out;
	}

	private static void display(final String offset, final MvnProject project) throws MalformedURLException, IOException, SAXException, ParserConfigurationException {
		offset(offset).println("|--- " + project.getName());
		offset(offset).println("|    url : " + project.getUrl());
		offset(offset).println("|");

		for (final MvnDependency dependency : project.getDependencies()) {
			System.out.println((offset.isEmpty() ? "" : "|" + offset) + "|--- " + dependency.getId() + "   " + dependency.getScope());
			if (!dependency.isOptional() && dependency.getScope() == MvnDependency.Scope.compile) {
				MvnProject myProject;
				try {
					myProject = loadProject("    " + offset, project, dependency.getId(), dependency.getId().getArtifactId());
				} catch (final FileNotFoundException e) {
					final String artifact = tocamelCase(dependency.getId().getArtifactId());
					myProject = loadProject("    " + offset, project, dependency.getId(), artifact);
				}
				display("    " + offset, myProject);
			}
		}
	}

	private static MvnProject loadProject(final String offset, final MvnProject project, final MvnId dependencyId, final String artifact) throws MalformedURLException, IOException, SAXException, ParserConfigurationException {
		String version = dependencyId.getVersion().trim();
		if (version.startsWith("${") && version.endsWith("}")) {
			version = project.getId().getVersion();
		}
		final String myUrl = "http://central.maven.org/maven2/" + dependencyId.getGroupId().replace('.', '/') + "/" + artifact + "/" + version + "/" + artifact + "-" + version + ".pom";
		return parse(myUrl);
	}

	private static MvnProject parse(final String httpUrl) throws MalformedURLException, IOException, SAXException, ParserConfigurationException {
		final URL url = new URL(httpUrl);
		//final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("172.20.0.9", 3128));
		final URLConnection urlConnection = url.openConnection();
		final int len = urlConnection.getContentLength();

		if (len < 0) {
			throw new RuntimeException("Content length unavailable.");
		} else if (len == 0) {
			throw new RuntimeException("No content available.");
		}

		final MvnHandler handler = new MvnHandler(httpUrl);
		SAXParserFactory.newInstance().newSAXParser().parse(urlConnection.getInputStream(), handler);
		return handler.getProject();
	}
}
