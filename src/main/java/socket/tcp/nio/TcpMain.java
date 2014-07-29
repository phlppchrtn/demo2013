package socket.tcp.nio;

import io.vertigo.nitro.tcp.io.resp.VCommandHandler;
import io.vertigo.nitro.tcp.protocol.ReqResp;
import socket.tcp.AbstractTcpMain;

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
