package socket.tcp.nio;

import socket.tcp.AbstractTcpMain;
import socket.tcp.io.resp.VCommandHandler;
import socket.tcp.protocol.ReqResp;

public final class TcpMain extends AbstractTcpMain {
	public static void main(String[] args) throws InterruptedException {
		new TcpMain().testSuite();
	}

	@Override
	protected Runnable createTcpServer(VCommandHandler commandHandler, int port) {
		return new TcpServer2(port, commandHandler);
	}

	@Override
	protected ReqResp createTcpClient(String host, int port) {
		return new TcpClient2(host, port);
	}
}
