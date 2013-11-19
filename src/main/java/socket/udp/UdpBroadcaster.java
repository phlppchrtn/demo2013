package socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
* @author pchretien
* @version $Id: RedisInvocationHandler.java,v 1.1 2013/10/09 14:02:58 pchretien Exp $ 
*/
public final class UdpBroadcaster {
	private static final int PORT = 4444;

	public void hello() throws IOException {

		try (final DatagramSocket datagramSocket = new DatagramSocket()) {
			datagramSocket.setBroadcast(true);
			//---
			final byte[] sendData = ("CALL ME").getBytes();
			//random port to send the package
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), PORT);
			datagramSocket.send(sendPacket);
		}
	}
}
