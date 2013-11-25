package socket.tcp.io;

import java.io.IOException;

import socket.tcp.AbstractTcpMain;
import socket.tcp.protocol.ReqResp;
import socket.tcp.protocol.VCommandHandler;

public final class TcpMain extends AbstractTcpMain {
	public static void main(String[] args) throws IOException, InterruptedException {
		new TcpMain().testSuite();
	}

	protected Runnable createTcpServer(VCommandHandler commandHandler, int port) {
		return new TcpServer(port, commandHandler);
	}

	protected ReqResp createTcpClient(String host, int port) {
		return new TcpClient(host, port);
	}
}
