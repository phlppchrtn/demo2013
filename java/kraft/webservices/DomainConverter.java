package kraft.webservices;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;

import kasper.domain.metamodel.Domain;
import kasper.domain.metamodel.Formatter;
import kasper.domain.metamodel.KDataType;
import kasper.kernel.exception.KRuntimeException;
import kasper.kernel.util.ClassUtil;
import kasperx.domain.formatter.FormatterDefault;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class DomainConverter implements JsonSerializer<Domain> {
	public JsonElement serialize(final Domain domain, final Type srcType, final JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", domain.getName());
		jsonObject.addProperty("dataType", domain.getDataType().name());
		if (domain.hasDtDefinition()) {
			jsonObject.addProperty("dtDefinitionName", domain.getDtDefinition().getName());
		}
		jsonObject.add("constraints", context.serialize(domain.getConstraints()));
		//Cas des formatters
		jsonObject.add("formatter", create(domain.getFormatter(), domain.getDataType()));
		return jsonObject;
	}

	private static JsonElement create(Formatter formatter, KDataType dataType) {
		JsonObject jsonFormatter = new JsonObject();
		jsonFormatter.addProperty("name", formatter.getName());
		switch (dataType) {
			case Long:
			case BigDecimal:
			case Double:
			case Integer:
				jsonFormatter.addProperty("pattern", getValue(formatter, dataType, "pattern"));
				break;
			case Date:
				jsonFormatter.addProperty("pattern", getValue(formatter, dataType, "pattern"));
				break;
			case Boolean:
				JsonObject jsonPattern = new JsonObject();
				jsonPattern.addProperty("true", getValue(formatter, dataType, "truePattern"));
				jsonPattern.addProperty("false", getValue(formatter, dataType, "falsePattern"));
				jsonFormatter.add("pattern", jsonPattern);
				break;
			case String:
				break;
			default:
				break;
		}
		return jsonFormatter;
	}

	private static String getValue(Formatter formatter, KDataType dataType, String fieldName) {
		if (formatter instanceof FormatterDefault) {
			return doGetValue(FormatterDefault.class.cast(formatter).getFormatter(dataType), fieldName);
		}
		return doGetValue(formatter, fieldName);
	}

	private static String doGetValue(Formatter formatter, String fieldName) {
		final Collection<Field> fields = ClassUtil.getAllFields(formatter.getClass());
		for (final Field field : fields) {
			if (field.getName().equals(fieldName)) {
				try {
					field.setAccessible(true);
					return (String) field.get(formatter);
				} catch (Exception e) {
					throw new KRuntimeException("accès impossible à la propriété '{0}' sur le formatter '{1}'", e, fieldName, formatter.getClass());
				}
			}
		}
		return null;
	}
}
