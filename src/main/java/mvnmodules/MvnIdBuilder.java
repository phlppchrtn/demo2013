package mvnmodules;
import io.vertigo.lang.Assertion;
import io.vertigo.lang.Builder;

public final class MvnIdBuilder implements Builder<MvnId> {
	private String myGroupId;
	private String myArtifactId;
	private String myVersion;

	public void withGroupId(final String groupId) {
		Assertion.checkArgNotEmpty(groupId);
		//-----
		myGroupId = groupId;
	}

	public void withArtifactId(final String artifactId) {
		Assertion.checkArgNotEmpty(artifactId);
		//-----
		myArtifactId = artifactId;
	}

	public void withVersion(final String version) {
		Assertion.checkArgNotEmpty(version);
		//-----
		myVersion = version;
	}

	@Override
	public MvnId build() {
		return new MvnId(myGroupId, myArtifactId, myVersion);
	}

}
