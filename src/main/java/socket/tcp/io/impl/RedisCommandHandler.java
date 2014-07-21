package socket.tcp.io.impl;

import socket.tcp.protocol.VCommand;
import socket.tcp.protocol.VCommandHandler;

public final class RedisCommandHandler implements VCommandHandler {
	private long datas;

	public String onCommand(VCommand command) {
		switch (command.getName()){
		case "flushdb":
			datas = 0;
			break;
		case"llen":
			break;
		case"lpush":
			datas++;
			break;
		default:
			throw new RuntimeException("Command inconnue :" + command.getName());
		}
		return ":" + datas;
	}
}