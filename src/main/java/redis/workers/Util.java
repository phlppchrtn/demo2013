package redis.workers;

import java.io.Serializable;

import kasper.codec.CodecManager;
import kasperimpl.codec.CodecManagerImpl;

final class Util {
	private static final CodecManager codecManager = new CodecManagerImpl();

	static String encode(final Object toEncode) {
		return codecManager.getBase64Codec().encode(codecManager.getSerializationCodec().encode((Serializable) toEncode));
	}

	static Object decode(final String encoded) {
		return codecManager.getSerializationCodec().decode(codecManager.getBase64Codec().decode(encoded));
	}
}
