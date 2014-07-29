package io.vertigo.nitro.tcp.io.resp;

public final class RespCommand {
	private final String name;
	private final String[] args;

	public RespCommand(String name, String... args) {
		if (name == null) {
			throw new NullPointerException("name is required");
		}
		if (args == null) {
			throw new NullPointerException("args is required, may be empty");
		}
		for (String item : args){
			if (item== null) {
				throw new NullPointerException("arg can not be null");
			}
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
