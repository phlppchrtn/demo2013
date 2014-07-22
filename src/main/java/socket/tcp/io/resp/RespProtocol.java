package socket.tcp.io.resp;

import io.vertigo.kernel.lang.Assertion;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

final class RespProtocol {

	private static final String CHARSET = "UTF-8";
	private static final String LN = "\r\n";

	//	static void encode(VCommand command, OutputStream out) throws IOException {
	//
	//	}

	private static void write(OutputStream out, String command, String args[]) throws IOException {
		//--- *Nb d'infos
		write(out, "*");
		write(out, String.valueOf(args.length + 1));
		write(out, LN);
		//--- cas du nom de la commande
		writeBulkString(out, command);
		//--- cas des args 
		for (String arg : args) {
			writeBulkString(out, arg);
		}
	}

	//
	//	static VCommand decode(BufferedReader input) throws IOException {
	//		String line = input.readLine();
	//		if (line == null) {
	//			return null;
	//		}
	//		//		System.out.println("sub: " + line.substring(1));
	//		if (!line.startsWith("*")) {
	//			throw new RuntimeException("protocol must begin with *");
	//		}
	//		final int n = Integer.valueOf(line.substring(1));
	//		if (n < 1) {
	//			throw new RuntimeException("protocol must contains at least one line");
	//		}
	//
	//		String commandName = null;
	//		String[] args = new String[n - 1];
	//		for (int i = 0; i < n; i++) {
	//			line = input.readLine();
	//			//	System.out.println("line('" + i + "') : " + line);
	//			if (!line.startsWith("$")) {
	//				throw new RuntimeException("protocol must contains lines with $");
	//			}
	//			//n = Integer.valueOf(line.substring(1)); 
	//			//On n'exploite pas cette info
	//			line = input.readLine();
	//			if (i == 0) {
	//				commandName = line;
	//				//	System.out.println("name  : " + line);
	//			} else {
	//				args[i - 1] = line;
	//			}
	//		}
	//		return new VCommand(commandName, args);
	//	}

	private static void write(OutputStream out, byte[] bytes) throws IOException {
		out.write(bytes);
	}

	private static void write(OutputStream out, String s) throws IOException {
		write(out, s.getBytes(CHARSET));
	}

	private static void writeBulkString(OutputStream out, String bulk) throws IOException {
		//System.out.println("bulk:" + bulk);
		//--- cas du nom de la commande
		byte[] bytes = bulk.getBytes(CHARSET);
		write(out, "$");
		write(out, String.valueOf(bytes.length));
		write(out, LN);
		write(out, bytes);
		write(out, LN);
	}

	static void push(BufferedOutputStream out, String command, String[] args) throws IOException {
		//System.out.println("exec command :" + command.getName());
		RespProtocol.write(out, command, args);
		out.flush();
	}

	static Object pull(BufferedReader in, char expected) throws IOException {
		String response = in.readLine();
		//System.out.println(expected + ":" + response);
		//---
		if ("*-1".equals(response)) {
			return null;
		}
		//----
		Assertion.checkArgument('?' == expected || expected == response.charAt(0), "exepected {0}, find {1}", expected, response.charAt(0));
		//----
		switch (response.charAt(0)) {
			case ':': //number
				return Long.valueOf(response.substring(1));
			case '+': //string
				return response.substring(1);
			case '$': //bulk 
				int n = Integer.valueOf(response.substring(1));
				if (n < 0) {
					return null;
				} else if (n == 0) {
					return "";
				}
				return in.readLine();
			case '*': //array
				int m = Integer.valueOf(response.substring(1));
				List list = new ArrayList<>();
				for (int i = 0; i < m; i++) {
					list.add(pull(in, '?'));
				}
				return list;
			default:
				throw new IllegalArgumentException(response);
		}

	}
}
