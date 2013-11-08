package misc.socket;


import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import org.json.JSONArray;
import org.json.JSONObject;

public class RadarSocketIo {
	public static void main(String[] args) throws Exception {
		SocketIO socket = new SocketIO("http://npiedeloup1:8090");
		/*socket.connect(new IOCallback() {
			@Override
			public void onMessage(JSONObject json, IOAcknowledge ack) {
				try {
					System.out.println("Server said:" + json.toString(2));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onMessage(String data, IOAcknowledge ack) {
				System.out.println("Server said: " + data);
			}

			@Override
			public void onError(SocketIOException socketIOException) {
				System.out.println("an Error occured");
				socketIOException.printStackTrace();
			}

			@Override
			public void onDisconnect() {
				System.out.println("Connection terminated.");
			}

			@Override
			public void onConnect() {
				System.out.println("Connection established");
			}

			@Override
			public void on(String event, IOAcknowledge ack, Object... args) {
				System.out.println("Server triggered event '" + event + "'");
			}
		});
		*/
		// This line is cached until the connection is establisched.
		socket.connect(new IOCallback() {

			@Override
			public void onMessage(String data, IOAcknowledge ack) {
			}

			@Override
			public void onError(SocketIOException socketIOException) {
				System.out.println("an Error occured");
				socketIOException.printStackTrace();
			}

			@Override
			public void onDisconnect() {
			}

			@Override
			public void onConnect() {
			}

			@Override
			public void on(String event, IOAcknowledge ack, Object... args) {
				System.out.println(">>>>on : " + event);
			}

			@Override
			public void onMessage(JSONObject json, IOAcknowledge ack) {
			}
		});
		Thread.sleep(2000);
		for (int i = 0; i < 50000; i++) {
			Thread.sleep(150);
			socket.emit("ping", new JSONArray("[" + i * 10 % 360 + ", " + i % 250 + ", 2]"));
		}
		//Thread.sleep(5000);
		socket.disconnect();
	}
}
