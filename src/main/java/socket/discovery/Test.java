package socket.discovery;

import java.io.IOException;

public class Test {
	public static void main(String[] args) throws IOException {
		new Thread(new Receiver()).start();
		Sender.hello();
	}
}
