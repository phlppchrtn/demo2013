package mvnmodules;
import io.vertigo.lang.Assertion;

public final class MvnId {
	private final String groupId;
	private final String artifactId;
	private final String version;

	MvnId(final String groupId, final String artifactId, final String version) {
		Assertion.checkArgNotEmpty(groupId);
		Assertion.checkArgNotEmpty(artifactId);
		//Assertion.checkArgNotEmpty(version);
		//-----
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return "{ groupId:" + groupId + ", artifactId:" + artifactId + ", version:" + version + " }";
	}
}
