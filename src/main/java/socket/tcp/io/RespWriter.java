package socket.tcp.io;

import java.io.IOException;
import java.io.OutputStream;

final class RespWriter {
	private static final String CHARSET = "UTF-8";
	private static final String LN = "\r\n";

	private final OutputStream output;

	RespWriter(OutputStream output) {
		this.output = output;
	}

	RespWriter write(byte[] bytes) throws IOException {
		output.write(bytes);
		return this;
	}

	RespWriter writeln() throws IOException {
		//System.out.println();
		return write(LN);
	}

	RespWriter write(String s) throws IOException {
		//System.out.print(s);
		return write(s.getBytes(CHARSET));
	}

	RespWriter writeBulkString(String bulk) throws IOException {
		//System.out.println("bulk:" + bulk);
		//--- cas du nom de la commande
		byte[] bytes = bulk.getBytes(CHARSET);
		return write("$")//
				.write(String.valueOf(bytes.length))//
				.write(LN)//
				.write(bytes)//
				.write(LN);
	}
}
