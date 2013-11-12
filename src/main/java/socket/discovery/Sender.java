package socket.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

//http://michieldemey.be/blog/network-discovery-using-udp-broadcast/
public class Sender {
	private static final int PORT = 4444;

	public static void hello() throws IOException {
		//Open a random port to send the package
		try (final DatagramSocket datagramSocket = new DatagramSocket()) {
			datagramSocket.setBroadcast(true);
			//---
			final byte[] sendData = "DISCOVER_VERTIGO".getBytes();

			//Try the 255.255.255.255 first
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), PORT);
			datagramSocket.send(sendPacket);
			System.out.println(">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
		}
		// Broadcast the message over all the network interfaces
		/*		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
				while (interfaces.hasMoreElements()) {
					NetworkInterface networkInterface = interfaces.nextElement();

							    if (networkInterface.isLoopback() || !networkInterface.isUp()) {
							      continue; // Don't want to broadcast to the loopback interface
							    }

					for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
						InetAddress broadcast = interfaceAddress.getBroadcast();
						if (broadcast == null) {
							continue;
						}

						// Send the broadcast package!
						try {
							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
							c.send(sendPacket);
						} catch (Exception e) {
						}

						System.out.println(">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
					}
				}

				System.out.println(">>> Done looping over all network interfaces. Now waiting for a reply!");
				//Wait for a response
				 */
	}
}
