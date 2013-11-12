package socket.discovery;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Receiver implements Runnable {
	private static final int PORT = 4444;

	public void run() {
		try (DatagramSocket socket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"))) {
			socket.setBroadcast(true);
			//---
			while (true) {
				System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");
				//Receive a packet
				final byte[] recvBuf = new byte[15000];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				socket.receive(packet);

				System.out.println(getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
				System.out.println(getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
