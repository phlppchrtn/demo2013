package socket.classloader.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

final class TcpCLClient implements AutoCloseable {
	private final Socket socket;
	private final InputStream in;
	private final PrintWriter out;

	TcpCLClient(String host, int port) {
		try {
			socket = new Socket(host, port);

			//			socket.setReuseAddress(true);
			//			socket.setKeepAlive(true); //Will monitor the TCP connection is valid
			//			socket.setTcpNoDelay(true); //Socket buffer Whetherclosed, to ensure timely delivery of data
			//			socket.setSoLinger(true, 0); //Control calls close () method, the underlying socket is closed immediately

			in = socket.getInputStream();
			out = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		try {
			try {
				//imbriquer les try
				out.close();
				in.close();
			} finally {
				//On ferme tjrs la socket
				socket.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] exec(String classname) throws IOException {
		push(classname);
		return pull();
	}

	private void push(String className) throws IOException {
		//System.out.println("exec command :" + command.getName());
		out.println(className);
		out.flush();
	}

	private byte[] pull() throws IOException {
		int next;

		StringBuilder sb = new StringBuilder();
		while ((next = in.read()) > -1) {
			if ((char) next == '*') {
				break;
			}
			sb.append((char) next);
		}
		int size = Integer.valueOf(sb.toString());
		System.out.println("attendus : " + size);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((next = in.read()) > -1) {
			//System.out.println(">>>" + next + " " + bos.size());
			bos.write(next);
			if (bos.size() == size) {
				break;
			}
		}
		System.out.println(">>> flush  ");
		bos.flush();
		return bos.toByteArray();
		/*
				int nRead;
				byte[] data = new byte[16384];

				while ((nRead = in.read(data, 0, data.length)) != -1) {
					System.out.println(">>>" + nRead + " " + System.currentTimeMillis());
					buffer.write(data, 0, nRead);
				}

				buffer.flush();*/

		//return buffer.toByteArray();
		//
		//		System.out.println(">>>" + nRead);
		//		buffer.flush();
		//
		//		return buffer.toByteArray();
	}
}
