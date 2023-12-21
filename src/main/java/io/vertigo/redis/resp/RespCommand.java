package io.vertigo.redis.resp;

import java.util.Arrays;

import io.vertigo.lang.Assertion;

public final class RespCommand {
	private final String command;
	private final String[] args;

	RespCommand(final String command, final String... args) {
		Assertion.checkNotNull(command, "a command is required");
		Assertion.checkNotNull(args, "args is required, may be empty");
		Arrays.stream(args).forEach(arg -> Assertion.checkNotNull(arg, "arg can not be null"));
		//---
		this.command = command.toLowerCase();
		this.args = args;
	}

	public String getName() {
		return command;
	}

	public String[] args() {
		return args;
	}
}
