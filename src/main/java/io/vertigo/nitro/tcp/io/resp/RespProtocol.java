package io.vertigo.nitro.tcp.io.resp;

import io.vertigo.kernel.lang.Assertion;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public final class RespProtocol {
	static enum RespType {
		RESP_STRING('+'), //
		RESP_ARRAY('*'), //
		RESP_BULK('$'), //
		RESP_INTEGER(':'), //
		RESP_EVAL('?');
		private final char c;

		private RespType(final char c) {
			this.c = c;
		}

		char getChar() {
			return c;
		}
	}

	private static final String CHARSET = "UTF-8";
	private static final String LN = "\r\n";

	private static void writeCommand(final OutputStream out, final String command, final String args[]) throws IOException {
		//--- *Nb d'infos
		out.write("*".getBytes(CHARSET));
		out.write(String.valueOf(args.length + 1).getBytes(CHARSET));
		out.write(LN.getBytes(CHARSET));
		//--- cas du nom de la commande
		writeBulkString(out, command);
		//--- cas des args 
		for (final String arg : args) {
			writeBulkString(out, arg);
		}
	}

	static RespCommand readCommand(final BufferedReader input) throws IOException {
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
		final String[] args = new String[n - 1];
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

	public static void writeLong(final OutputStream out, final Long value) throws IOException {
		out.write(":".getBytes(CHARSET));
		out.write(String.valueOf(value).getBytes(CHARSET));
		out.write(LN.getBytes(CHARSET));
	}

	public static void writeError(final OutputStream out, final String msg) throws IOException {
		out.write("-".getBytes(CHARSET));
		out.write(msg.getBytes(CHARSET));
		out.write(LN.getBytes(CHARSET));
	}

	public static void writeSimpleString(final OutputStream out, final String value) throws IOException {
		out.write("+".getBytes(CHARSET));
		out.write(value.getBytes(CHARSET));
		out.write(LN.getBytes(CHARSET));
	}

	public static void writeBulkString(final OutputStream out, final String bulk) throws IOException {
		//System.out.println("bulk:" + bulk);
		//--- cas du nom de la commande
		if (bulk == null) {
			out.write("$-1".getBytes(CHARSET));
			out.write(LN.getBytes(CHARSET));
			return;
		}

		final byte[] bytes = bulk.getBytes(CHARSET);
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
	static Object pushPull(final RespType type, final BufferedReader in, final BufferedOutputStream out, final String command, final String[] args) {
		try {
			push(out, command, args);
			return pull(in, type.getChar());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void push(final BufferedOutputStream out, final String command, final String[] args) throws IOException {
		//System.out.println("exec command :" + command.getName());
		writeCommand(out, command, args);
		out.flush();
	}

	private static Object pull(final BufferedReader in, final char expected) throws IOException {
		final String response = in.readLine();
		//System.out.println(expected + ":" + response);
		//---
		final char start = response.charAt(0);
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
				final int n = Integer.valueOf(response.substring(1));
				if (n < 0) {
					return null;
				} else if (n == 0) {
					return "";
				}
				return in.readLine();
			case '*': //array
				final int m = Integer.valueOf(response.substring(1));
				final List list = new ArrayList<>();
				for (int i = 0; i < m; i++) {
					list.add(pull(in, '?'));
				}
				return list;
			default:
				throw new IllegalArgumentException("According resp protocol, a response must starts with - + : $ or *");
		}

	}

}
