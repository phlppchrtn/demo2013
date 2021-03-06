package socket.tcp.nio;

import java.io.IOException;
import java.nio.ByteBuffer;

import socket.tcp.protocol.VCommand;

final class RedisProtocol2 {
	private static final String LN = "\r\n";
	public static final String CHARSET = "UTF-8";

	public static void encode(final VCommand command, final ByteBuffer buffer) throws IOException {
		//--- *Nb d'infos
		//--- cas du nom de la commande
		byte[] bytes = command.getName().getBytes(CHARSET);
		buffer
				.put("*".getBytes(CHARSET))
				.put(String.valueOf(command.args().length + 1).getBytes(CHARSET))
				.put(LN.getBytes(CHARSET))
				.put("$".getBytes(CHARSET))
				.put(String.valueOf(bytes.length).getBytes(CHARSET))
				.put(LN.getBytes(CHARSET))
				.put(bytes)
				.put(LN.getBytes(CHARSET));
		//--- cas des args 
		for (final String arg : command.args()) {
			bytes = arg.getBytes(CHARSET);
			buffer.put("$".getBytes(CHARSET))
					.put(String.valueOf(bytes.length).getBytes(CHARSET))
					.put(LN.getBytes(CHARSET))
					.put(bytes)
					.put(LN.getBytes(CHARSET));
		}
	}

	public static VCommand decode(final ByteBuffer input) throws IOException {
		final String[] lines = new String(input.array(), CHARSET).split(LN);
		if (lines.length == 0) {
			return null;
		}
		//		System.out.println("sub: " + line.substring(1));
		if (!lines[0].startsWith("*")) {
			throw new RuntimeException("protocol must begin with *");
		}
		final int n = Integer.valueOf(lines[0].substring(1));
		if (n < 1) {
			throw new RuntimeException("protocol must contains at least one line");
		}

		String commandName = null;
		final String[] args = new String[n - 1];
		for (int i = 0; i < n; i++) {
			String line = lines[2 * i + 1];
			//			System.out.println("line('" + 2 * i + "') : " + line);
			if (!line.startsWith("$")) {
				throw new RuntimeException("protocol must contains lines with $ :" + line);
			}
			//n = Integer.valueOf(line.substring(1)); 
			//On n'exploite pas cette info
			line = lines[2 * i + 2];
			if (i == 0) {
				commandName = line;
				//System.out.println("name  : " + line);
			} else {
				args[i - 1] = line;
			}
		}
		return new VCommand(commandName, args);
	}
}
