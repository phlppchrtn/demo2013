package mvnmodules;

import io.vertigo.lang.Assertion;

import java.util.List;

public final class MvnProject {
	private final String name;
	private final MvnId id;
	private final List<MvnDependency> dependencies;
	private final String url;

	MvnProject(final String url, final MvnId id, final String name, final List<MvnDependency> dependencies) {
		Assertion.checkArgNotEmpty(url);
		Assertion.checkNotNull(id, "id required for url {0}", url);
		Assertion.checkNotNull(dependencies);
		//-----
		this.id = id;
		this.url = url;
		this.name = name;
		this.dependencies = dependencies;
	}

	public MvnId getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public String getName() {
		return name;
	}

	public List<MvnDependency> getDependencies() {
		return dependencies;
	}

	@Override
	public String toString() {
		return "{ name:" + name == null ? "" : name + ", id:" + id + ", dependencies:" + dependencies + ", url:" + url + " }";
	}
}
