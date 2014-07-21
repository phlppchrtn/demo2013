package socket.tcp.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

import socket.tcp.protocol.VCommand;

final class RespProtocol {

	public static void encode(VCommand command, OutputStream output) throws IOException {
		RespWriter writer = new RespWriter(output)//
				//--- *Nb d'infos
				.write("*")//
				.write(String.valueOf(command.args().length + 1))//
				.writeln()//
				//--- cas du nom de la commande
				.writeBulkString(command.getName());
		//--- cas des args 
		for (String arg : command.args()) {
			writer.writeBulkString(arg);
		}
	}

	public static VCommand decode(BufferedReader input) throws IOException {
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
		return new VCommand(commandName, args);
	}
}
