package socket.tcp.io;

import java.io.IOException;
import java.io.OutputStream;

final class Writer {
	private static final String CHARSET = "UTF-8";
	private static final String LN = "\r\n";

	private final OutputStream output;

	Writer(OutputStream output) {
		this.output = output;
	}

	Writer write(byte[] bytes) throws IOException {
		output.write(bytes);
		return this;
	}

	Writer writeln() throws IOException {
		return write(LN);
	}

	Writer write(String s) throws IOException {
		return write(s.getBytes(CHARSET));
	}

	Writer writeBulkString(String bulk) throws IOException {
		//--- cas du nom de la commande
		byte[] bytes = bulk.getBytes(CHARSET);
		return write("$")//
				.write(String.valueOf(bytes.length))//
				.write(LN)//
				.write(bytes)//
				.write(LN);
	}
}
