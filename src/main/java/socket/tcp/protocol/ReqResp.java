package socket.tcp.protocol;

import java.io.IOException;

//Patterns 
public interface ReqResp extends AutoCloseable {
	long exec(Command command) throws IOException;

	//On simplifie le close pour éliminer les exceptions
	void close();
}
