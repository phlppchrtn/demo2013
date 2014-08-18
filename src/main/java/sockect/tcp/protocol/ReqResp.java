package sockect.tcp.protocol;

import java.io.IOException;

// Protocols 
// Request / Reply
public interface ReqResp extends AutoCloseable {
	long exec(VCommand command) throws IOException;

	//On simplifie le close pour �liminer les exceptions
	void close();
}
