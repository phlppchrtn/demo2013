package socket.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public class RedisProtocol {
	private static final String LN = "\r\n";
	public static final String CHARSET = "UTF-8";

	public static void encode(Command command, OutputStream output) throws IOException {
		byte[] bytes;

		//--- *Nb d'infos
		output.write("*".getBytes(CHARSET));
		output.write(String.valueOf(command.args().length + 1).getBytes(CHARSET));
		output.write(LN.getBytes(CHARSET));
		//--- cas du nom de la commande
		bytes = command.getName().getBytes(CHARSET);
		output.write("$".getBytes(CHARSET));
		output.write(String.valueOf(bytes.length).getBytes(CHARSET));
		output.write(LN.getBytes(CHARSET));
		output.write(bytes);
		output.write(LN.getBytes(CHARSET));
		//--- cas des args 
		for (String arg : command.args()) {
			bytes = arg.getBytes(CHARSET);
			output.write("$".getBytes(CHARSET));
			output.write(String.valueOf(bytes.length).getBytes(CHARSET));
			output.write(LN.getBytes(CHARSET));
			output.write(bytes);
			output.write(LN.getBytes(CHARSET));
		}
	}

	public static Command decode(BufferedReader input) throws IOException {
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
		return new Command(commandName, args);
	}
}
