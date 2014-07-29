package io.vertigo.nitro.tcp.protocol;

public final class VCommand {
	private final String name;
	private final String[] args;

	public VCommand(String name, String... args) {
		if (name == null) {
			throw new NullPointerException("name is required");
		}
		if (args == null) {
			throw new NullPointerException("args is required, may be empty");
		}
		//-------------------------------------------------
		this.name = name;
		this.args = args;
	}

	public String getName() {
		return name;
	}

	public String[] args() {
		return args;
	}
}
