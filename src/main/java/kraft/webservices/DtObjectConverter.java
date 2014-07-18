package kraft.webservices;

import java.lang.reflect.Type;

import kasper.domain.metamodel.DtDefinition;
import kasper.domain.metamodel.DtField;
import kasper.domain.metamodel.Formatter;
import kasper.domain.metamodel.KDataType;
import kasper.domain.model.DtObject;
import kasper.domain.util.DtObjectUtil;
import kasperx.domain.formatter.FormatterDefault;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class DtObjectConverter implements JsonSerializer<DtObject>, JsonDeserializer<DtObject> {
	public static final ThreadLocal<DtDefinition> SHARED = new ThreadLocal<DtDefinition>();

	public JsonElement serialize(final DtObject dto, final Type srcType, final JsonSerializationContext context) {
		JsonObject jsonObject = new JsonObject();
		DtDefinition dtDefinition = DtObjectUtil.findDtDefinition(dto);
		for (DtField dtField : dtDefinition.getFields()) {
			Object value = dtField.getDataAccessor().getValue(dto);
			jsonObject.add(dtField.getName(), toJson(value, dtField.getDomain().getDataType()));
		}
		return jsonObject;
	}

	public DtObject deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
		DtDefinition dtDefinition = SHARED.get();
		DtObject dto = DtObjectUtil.createDtObject(dtDefinition);

		JsonObject jdto = (JsonObject) json;
		for (DtField dtField : dtDefinition.getFields()) {
			if (jdto.has(dtField.getName())) {
				JsonElement element = jdto.get(dtField.getName());
				Object value = fromJson(element, dtField.getDomain().getDataType());
				dtField.getDataAccessor().setValue(dto, value);
			}
		}
		return dto;
	}

	private static final Formatter FORMATTER = new FormatterDefault("FMT_JSON");

	private static Object fromJson(JsonElement element, KDataType dataType) {
		if (element.equals(JsonNull.INSTANCE)) {
			return null;
		}
		switch (dataType) {
			case BigDecimal:
				return element.getAsBigDecimal();
			case Double:
				return element.getAsDouble();
			case Integer:
				return element.getAsInt();
			case Long:
				return element.getAsLong();
			case Boolean:
				return element.getAsBoolean();
			case Date:
				return FORMATTER.stringToValue(element.getAsString(), KDataType.Date);
			case String:
				return element.getAsString();
		}
		throw new IllegalAccessError();
	}

	private static JsonElement toJson(Object value, KDataType dataType) {
		if (value == null)
			return JsonNull.INSTANCE;
		switch (dataType) {
			case BigDecimal:
			case Double:
			case Integer:
			case Long:
				return new JsonPrimitive((Number) value);
			case Boolean:
				return new JsonPrimitive((Boolean) value);
			case String:
				return new JsonPrimitive((String) value);
			case Date:
				return new JsonPrimitive(FORMATTER.valueToString(value, KDataType.Date));
		}
		throw new IllegalAccessError();
	}
}
