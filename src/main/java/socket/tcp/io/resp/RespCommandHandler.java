package socket.tcp.io.resp;

import java.io.IOException;
import java.io.OutputStream;

public interface RespCommandHandler {
	void onCommand(OutputStream out, RespCommand command) throws IOException;
}
