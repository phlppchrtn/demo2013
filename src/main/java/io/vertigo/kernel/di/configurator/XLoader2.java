package io.vertigo.kernel.di.configurator;

import io.vertigo.core.lang.Assertion;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class XLoader2 {
	private static final class XParser {
		void parse(String resource) {
			//
		}
	}

	private final Map<String, XParser> parsers = new HashMap<>();
	private final Map<String, String> resources = new HashMap<>();

	public void registerParser(String type, XParser parser) {
		Assertion.checkArgNotEmpty(type);
		Assertion.checkNotNull(parser);
		Assertion.checkArgument(!parsers.containsKey(type), "type '{0}' of parser is already registered");
		//---------------------------------------------------------------------
		parsers.put(type, parser);
	}

	public void addResource(String type, String resource) {
		Assertion.checkArgNotEmpty(type);
		Assertion.checkArgNotEmpty(resource);
		Assertion.checkArgument(parsers.containsKey(type), "type '{0}' of parser is not registered");
		//---------------------------------------------------------------------
		resources.put(type, resource);
	}

	public void load() {
		for (Entry<String, String> entry : resources.entrySet()) {
			final String type = entry.getKey();
			final String resource = entry.getValue();
			parsers.get(type).parse(resource);
		}
	}
}
