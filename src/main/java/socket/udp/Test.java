package socket.udp;

import java.io.IOException;

public class Test {
	public static void main(String[] args) throws IOException {
		new Thread(new UdpReceiver()).start();
		new UdpBroadcaster().hello();
	}
}
