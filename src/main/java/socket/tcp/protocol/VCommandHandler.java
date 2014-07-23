package socket.tcp.protocol;

import socket.tcp.protocol.VCommand;

public interface VCommandHandler {
	String onCommand(VCommand command);
}
