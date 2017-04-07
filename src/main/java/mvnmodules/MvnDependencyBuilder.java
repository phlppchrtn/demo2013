package mvnmodules;

import io.vertigo.lang.Assertion;
import io.vertigo.lang.Builder;
import io.vertigo.util.ListBuilder;

public final class MvnDependencyBuilder implements Builder<MvnDependency> {
	private final MvnIdBuilder myMvnIdBuilder = new MvnIdBuilder();
	private String myScope = "compile"; //default
	private boolean myOptional = false;//default

	private final ListBuilder<MvnExclusion> myExclusions = new ListBuilder<>();
	private final MvnProjectBuilder projectBuilder;

	MvnDependencyBuilder(final MvnProjectBuilder projectBuilder) {
		Assertion.checkNotNull(projectBuilder);
		//-----
		this.projectBuilder = projectBuilder;
	}

	public MvnIdBuilder id() {
		return myMvnIdBuilder;
	}

	public MvnDependencyBuilder withOptional(final boolean optional) {
		myOptional = optional;
		return this;
	}

	public MvnDependencyBuilder excludes(final String groupId, final String artifactId) {
		myExclusions.add(new MvnExclusion(groupId, artifactId));
		return this;
	}

	public MvnDependencyBuilder withScope(final String scope) {
		Assertion.checkArgNotEmpty(scope);
		myScope = scope;
		return this;
	}

	public MvnProjectBuilder endDependency() {
		return projectBuilder.withDependency(build());
	}

	@Override
	public MvnDependency build() {
		return new MvnDependency(myMvnIdBuilder.build(), myScope, myOptional, myExclusions.unmodifiable().build());
	}

}
