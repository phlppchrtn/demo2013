package socket.tcp.io;

import socket.tcp.AbstractTcpMain;
import socket.tcp.io.resp.RespClient;
import socket.tcp.io.resp.VCommandHandler;
import socket.tcp.protocol.ReqResp;

public final class TestTcpMain extends AbstractTcpMain {
	public static void main(String[] args) throws InterruptedException {
		new TestTcpMain().testSuite();
	}

	@Override
	protected Runnable createTcpServer(VCommandHandler commandHandler, int port) {
		return new TcpServer(port, commandHandler);
	}

	@Override
	protected ReqResp createTcpClient(String host, int port) {
		return new RespClient(host, port);
	}
}
