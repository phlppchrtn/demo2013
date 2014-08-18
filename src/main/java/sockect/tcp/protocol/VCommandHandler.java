package sockect.tcp.protocol;

import sockect.tcp.protocol.VCommand;

public interface VCommandHandler {
	String onCommand(VCommand command);
}
