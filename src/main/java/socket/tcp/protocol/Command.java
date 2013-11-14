package socket.tcp.protocol;

public final class Command {
	private final String name;
	private final String[] args;

	public Command(String name, String... args) {
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
