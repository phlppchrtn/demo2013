package io.vertigo.nitro.tcp.protocol;

import io.vertigo.nitro.tcp.protocol.VCommand;

public interface VCommandHandler {
	String onCommand(VCommand command);
}
