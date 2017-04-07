package mvnmodules;

import io.vertigo.lang.Assertion;

import java.util.List;

public final class MvnDependency {
	public static enum Scope {
		compile,
		provided,
		system,
		test,
		runtime
	}

	private final MvnId id;
	private final Scope scope;
	private final boolean optional;
	private final List<MvnExclusion> exclusions;

	MvnDependency(final MvnId id, final String scope, final boolean optional, final List<MvnExclusion> exclusions) {
		Assertion.checkNotNull(id);
		Assertion.checkArgNotEmpty(scope);
		Assertion.checkNotNull(exclusions);
		//-----
		this.id = id;
		this.scope = Scope.valueOf(scope);
		this.optional = optional;
		this.exclusions = exclusions;
	}

	public MvnId getId() {
		return id;
	}

	public Scope getScope() {
		return scope;
	}

	public boolean isOptional() {
		return optional;
	}

	public List<MvnExclusion> getExclusions() {
		return exclusions;
	}

	@Override
	public String toString() {
		return "{ id:" + id + ", scope:" + scope + ", optional:" + optional + " exclusions:" + exclusions + " }";
	}
}
