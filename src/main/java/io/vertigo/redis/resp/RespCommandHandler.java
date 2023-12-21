package io.vertigo.redis.resp;

import java.io.IOException;

@FunctionalInterface
public interface RespCommandHandler {
	void onCommand(RespWriter writer, RespCommand command) throws IOException;
}
