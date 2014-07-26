package socket.tcp.io.resp;

import io.vertigo.kernel.lang.Assertion;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public final class RespProtocol {
	static enum RespType {
		RESP_STRING('+'), RESP_ARRAY('*'), RESP_BULK('$'), RESP_INTEGER(':'), RESP_EVAL('?');
		private final char c;

		private RespType(char c) {
			this.c = c;
		}

		char getChar() {
			return c;
		}
	}

	private static final String CHARSET = "UTF-8";
	private static final String LN = "\r\n";

	private static void writeCommand(OutputStream out, String command, String args[]) throws IOException {
		//--- *Nb d'infos
		out.write("*".getBytes(CHARSET));
		out.write(String.valueOf(args.length + 1).getBytes(CHARSET));
		out.write(LN.getBytes(CHARSET));
		//--- cas du nom de la commande
		writeBulkString(out, command);
		//--- cas des args 
		for (String arg : args) {
			writeBulkString(out, arg);
		}
	}

	static RespCommand readCommand(BufferedReader input) throws IOException {
		String line = input.readLine();
		if (line == null) {
			return null;
		}
		//		System.out.println("sub: " + line.substring(1));
		if (!line.startsWith("*")) {
			throw new RuntimeException("protocol must begin with *");
		}
		final int n = Integer.valueOf(line.substring(1));
		if (n < 1) {
			throw new RuntimeException("protocol must contains at least one line");
		}

		String commandName = null;
		String[] args = new String[n - 1];
		for (int i = 0; i < n; i++) {
			line = input.readLine();
			//	System.out.println("line('" + i + "') : " + line);
			if (!line.startsWith("$")) {
				throw new RuntimeException("protocol must contains lines with $");
			}
			//n = Integer.valueOf(line.substring(1)); 
			//On n'exploite pas cette info
			line = input.readLine();
			if (i == 0) {
				commandName = line;
				//	System.out.println("name  : " + line);
			} else {
				args[i - 1] = line;
			}
		}
		return new RespCommand(commandName, args);
	}

	public static void writeLong(OutputStream out, Long value) throws IOException {
		out.write(":".getBytes(CHARSET));
		out.write(String.valueOf(value).getBytes(CHARSET));
		out.write(LN.getBytes(CHARSET));
	}

	public static void writeError(OutputStream out, String msg) throws IOException {
		out.write("-".getBytes(CHARSET));
		out.write(msg.getBytes(CHARSET));
		out.write(LN.getBytes(CHARSET));
	}

	public static void writeSimpleString(OutputStream out, String value) throws IOException {
		out.write("+".getBytes(CHARSET));
		out.write(value.getBytes(CHARSET));
		out.write(LN.getBytes(CHARSET));
	}

	public static void writeBulkString(OutputStream out, String bulk) throws IOException {
		//System.out.println("bulk:" + bulk);
		//--- cas du nom de la commande
		if (bulk==null){
			out.write("$-1".getBytes(CHARSET));
			out.write(LN.getBytes(CHARSET));
			return;
		}	

		byte[] bytes = bulk.getBytes(CHARSET);
		out.write("$".getBytes(CHARSET));
		out.write(String.valueOf(bytes.length).getBytes(CHARSET));
		out.write(LN.getBytes(CHARSET));
		out.write(bytes);
		out.write(LN.getBytes(CHARSET));
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @param command
	 * @param args
	 * @param expected  T
	 * @return
	 * @throws IOException
	 */
	static Object pushPull(RespType type, BufferedReader in, BufferedOutputStream out, String command, String[] args) {
		try {
			push(out, command, args);
			return pull(in, type.getChar());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void push(BufferedOutputStream out, String command, String[] args) throws IOException {
		//System.out.println("exec command :" + command.getName());
		writeCommand(out, command, args);
		out.flush();
	}

	private static Object pull(BufferedReader in, char expected) throws IOException {
		String response = in.readLine();
		//System.out.println(expected + ":" + response);
		//---
		char start = response.charAt(0);
		if (start == '-') {
			throw new RuntimeException(response);
		}
		//Hack pour gérer un mauvais retour de brpoplpush
		if (start == '*' && "*-1".equals(response)) {
			return null;
		}
		//----
		Assertion.checkArgument('?' == expected || expected == response.charAt(0), "exepected {0}, find {1}", expected, response.charAt(0));
		//----
		switch (start) {
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
				throw new IllegalArgumentException("According resp protocol, a response must starts with - + : $ or *");
		}

	}

}
