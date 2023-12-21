package io.vertigo.redis.resp;

enum RespType {
	RESP_STRING('+'),
	RESP_ARRAY('*'),
	RESP_BULK('$'),
	RESP_INTEGER(':'),
	RESP_EVAL('?');
	private final char c;

	private RespType(final char c) {
		this.c = c;
	}

	char getChar() {
		return c;
	}
}
