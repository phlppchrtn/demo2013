package mvnmodules;

import io.vertigo.lang.Assertion;
import io.vertigo.lang.Builder;
import io.vertigo.util.ListBuilder;

public final class MvnProjectBuilder implements Builder<MvnProject> {
	private final String url;
	private final ListBuilder<MvnDependency> dependencies = new ListBuilder<>();
	private String myName;
	private final MvnIdBuilder myIdBuilder = new MvnIdBuilder();

	public MvnProjectBuilder(final String url) {
		this.url = url;
	}

	public MvnProjectBuilder withName(final String name) {
		Assertion.checkArgNotEmpty(name);
		//-----
		myName = name;
		return this;
	}

	//	public MvnProjectBuilder require(String groupId, String artifactId, String version) {
	//		dependencies.add(new MvnDependency(groupId, artifactId, version, false, Collections.<MvnExclusion> emptyList()));
	//		return this;
	//	}

	public MvnProjectBuilder withDependency(final MvnDependency dependency) {
		Assertion.checkNotNull(dependency);
		//-----
		dependencies.add(dependency);
		return this;

	}

	public MvnDependencyBuilder beginDependency() {
		return new MvnDependencyBuilder(this);
	}

	@Override
	public MvnProject build() {
		return new MvnProject(url, myIdBuilder.build(), myName, dependencies.unmodifiable().build());
	}

	public MvnIdBuilder id() {
		return myIdBuilder;
	}
}
