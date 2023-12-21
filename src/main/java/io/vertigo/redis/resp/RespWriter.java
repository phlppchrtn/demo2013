package io.vertigo.redis.resp;

import java.io.IOException;
import java.io.OutputStream;

import io.vertigo.lang.Assertion;

public final class RespWriter {
	private final OutputStream out;

	RespWriter(final OutputStream out) {
		Assertion.checkNotNull(out);
		//-----
		this.out = out;
	}

	public RespWriter writeOK() throws IOException {
		return writeSimpleString("OK");
	}

	public RespWriter writeError(final String msg) throws IOException {
		return write("-").writeLN(msg);
	}

	public RespWriter writeSimpleString(final String value) throws IOException {
		return write("+").writeLN(value);
	}

	public RespWriter writeLong(final Long value) throws IOException {
		return write(":").writeLN(String.valueOf(value));
	}

	public RespWriter writeBulkString(final String bulk) throws IOException {
		//System.out.println("bulk:" + bulk);
		//--- cas du nom de la commande
		if (bulk == null) {
			return writeLN("$-1");
		}

		final byte[] bytes = bulk.getBytes(RespProtocol.CHARSET);
		return write("$").write(bytes.length).writeLN()
				.writeLN(bulk);
	}

	public void writeCommand(final RespCommand command) throws IOException {
		//--- *Nb d'infos
		write("*").write(command.args().length + 1).writeLN()
				//--- cas du nom de la commande
				.writeBulkString(command.getName());
		//--- cas des args
		for (final String arg : command.args()) {
			writeBulkString(arg);
		}
		flush();
	}

	//	//exec
	//	public void writeCommands(final List<RespCommand> commands) throws IOException {
	//		write("*").write(commands.size()).writeLN()
	//				//--- cas du nom de la commande
	//				.writeBulkString("exec");
	//		for (final RespCommand command : commands) {
	//			doWriteCommand(command);
	//		}
	//		writeOK();
	//		flush();
	//	}

	public RespWriter write(final String s) throws IOException {
		System.out.print(s);
		out.write(s.getBytes(RespProtocol.CHARSET));
		return this;
	}

	public RespWriter write(final int i) throws IOException {
		return write(String.valueOf(i));
	}

	public RespWriter writeLN() throws IOException {
		return write(RespProtocol.LN);
	}

	private RespWriter writeLN(final String s) throws IOException {
		return write(s).writeLN();
	}

	public void flush() throws IOException {
		out.flush();
	}

	void close() throws IOException {
		out.close();
	}

}
