package mvnmodules;

public final class MvnExclusion {
	private final String groupId;
	private final String artifactId;

	MvnExclusion(final String groupId, final String artifactId) {
		this.groupId = groupId;
		this.artifactId = artifactId;

	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getGroupId() {
		return groupId;
	}

	@Override
	public String toString() {
		return "{ groupId:" + groupId + ", artifactId:" + artifactId + " }";
	}
}
