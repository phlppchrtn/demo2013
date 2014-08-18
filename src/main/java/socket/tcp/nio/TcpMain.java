package socket.tcp.nio;

import sockect.tcp.protocol.ReqResp;
import sockect.tcp.protocol.VCommandHandler;
import socket.tcp.AbstractTcpMain;

public final class TcpMain extends AbstractTcpMain {
	public static void main(final String[] args) throws InterruptedException {
		new TcpMain().testSuite();
	}

	@Override
	protected Runnable createTcpServer(final VCommandHandler commandHandler, final int port) {
		return new TcpServer2(port, commandHandler);
	}

	@Override
	protected ReqResp createTcpClient(final String host, final int port) {
		return new TcpClient2(host, port);
	}
}
