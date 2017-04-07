package mvnmodules;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class MvnHandler extends DefaultHandler {
	private final MvnProjectBuilder projectBuilder;
	private MvnDependencyBuilder dependencyBuilder;

	private String content;
	private String currentPath = "";

	MvnHandler(final String url) {
		projectBuilder = new MvnProjectBuilder(url);
	}

	public MvnProject getProject() {
		return projectBuilder.build();
	}

	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
		content = null;
		currentPath = currentPath.isEmpty() ? qName : currentPath + "." + qName;
		//	System.out.println(">>>" + currentPath + "  " + currentPath.length());
		switch (currentPath) {
			case "project.dependencies.dependency":
				dependencyBuilder = projectBuilder.beginDependency();
				break;
			default:
		}
	}

	@Override
	public void endElement(final String uri, final String localName, final String qName) throws SAXException {
		//		System.out.println("<<<<<" + currentPath + "== " + content);
		switch (currentPath) {
			case "project.parent.version":
			case "project.version":
				projectBuilder.id().withVersion(content);
				break;
			case "project.parent.artifactId":
			case "project.artifactId":
				projectBuilder.id().withArtifactId(content);
				break;
			case "project.parent.groupId":
			case "project.groupId":
				projectBuilder.id().withGroupId(content);
				break;
			case "project.name":
				projectBuilder.withName(content);
				break;
			case "project.dependencies.dependency":
				dependencyBuilder.endDependency();
				dependencyBuilder = null;
				break;
			case "project.dependencies.dependency.groupId":
				dependencyBuilder.id().withGroupId(content);
				break;
			case "project.dependencies.dependency.artifactId":
				dependencyBuilder.id().withArtifactId(content);
				break;
			case "project.dependencies.dependency.version":
				dependencyBuilder.id().withVersion(content);
				break;
			case "project.dependencies.dependency.scope":
				dependencyBuilder.withScope(content.toLowerCase());
				break;
			case "project.dependencies.dependency.optional":
				dependencyBuilder.withOptional(Boolean.valueOf(content));
				break;
			default:
				//
		}
		if (qName.length() == currentPath.length()) {
			currentPath = "";
		} else {
			currentPath = currentPath.substring(0, currentPath.length() - ("." + qName).length());
		}
	}

	@Override
	public void characters(final char ch[], final int start, final int length) throws SAXException {
		content = new String(ch, start, length);
	}
}
