package kraft.webservices;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import kasper.domain.metamodel.Constraint;
import kasper.domain.metamodel.Domain;
import kasper.domain.metamodel.DtDefinition;
import kasper.domain.metamodel.DtField;
import kasper.domain.metamodel.association.AssociationNode;
import kasper.kernel.Home;
import kasper.kernel.lang.MessageText;
import kasper.kernel.metamodel.Definition;
import kasper.kernel.metamodel.DefinitionReference;
import kasperx.domain.formatter.FormatterDate;
import kasperx.domain.formatter.FormatterNumber;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Path("/definitions")
public class Definitions {
	private final Gson gson = createGson();

	private static Gson createGson() {
		return new GsonBuilder()//
				.setPrettyPrinting()//
				.registerTypeAdapter(Domain.class, new DomainConverter())//
				.registerTypeAdapter(DefinitionReference.class, new DefinitionReferenceConverter())//
				.registerTypeHierarchyAdapter(Constraint.class, new ConstraintConverter())//
				.registerTypeHierarchyAdapter(FormatterNumber.class, new FormatterNumberConverter())//
				.registerTypeAdapter(MessageText.class, new MessageTextConverter())//
				.setExclusionStrategies(new FieldExclusion(AssociationNode.class, "associationDefinition"))//
				.setExclusionStrategies(new FieldExclusion(DtDefinition.class, "mappedFields"))//
				.setExclusionStrategies(new FieldExclusion(DtField.class, "dataAccessor"))//
				.setExclusionStrategies(new FieldExclusion(FormatterDate.class, "lstExFillInFormat"))//
				.setExclusionStrategies(new FieldExclusion(Domain.class, "properties"))// 
				.setExclusionStrategies(new FieldExclusion(DtField.class, "id"))//
				.setExclusionStrategies(new FieldExclusion(DtField.class, "persistent"))//
				.setExclusionStrategies(new FieldExclusion(DtDefinition.class, "localName"))//
				.setExclusionStrategies(new FieldExclusion(DtDefinition.class, "packageName"))//
				.setExclusionStrategies(new FieldExclusion(DtDefinition.class, "persistent"))//
				.create();
	}

	@GET
	@Produces("application/json")
	public String getAllDefinitions() {
		final Collection<Class<? extends Definition>> types = Home.getNameSpace().getAllTypes();
		final List list = new ArrayList<String>();
		for (final Class type : types) {
			list.add(type.getCanonicalName());
		}
		return gson.toJson(list);
	}

	@GET
	@Path("/{definitions}")
	@Produces("application/json")
	public String getAllDefinitions(@PathParam("definitions") final String definitionType) {
		return gson.toJson(Home.getNameSpace().getAll(getDefinitionClass(definitionType)));
	}

	private static Class<? extends Definition> getDefinitionClass(final String definitionType) {
		for (Class<? extends Definition> definitionClass : Home.getNameSpace().getAllTypes()) {
			if ((definitionClass.getSimpleName() + "s").equalsIgnoreCase(definitionType)) {
				return definitionClass;
			}
		}
		return null;
	}

	@GET
	@Path("/{definitions}/{id}")
	@Produces("application/json")
	public String getAllDefinitions(@PathParam("definitions") final String definitionType, @PathParam("id") final String id) {
		return gson.toJson(Home.getNameSpace().resolve(id, getDefinitionClass(definitionType)));
	}

	public static final class FormatterNumberConverter implements JsonSerializer<FormatterNumber> {
		public JsonElement serialize(final FormatterNumber formatterNumber, final Type srcType, final JsonSerializationContext context) {
			JsonObject jobject = new JsonObject();
			jobject.addProperty("name", formatterNumber.getName());
			jobject.addProperty("pattern", formatterNumber.getPattern());
			return jobject;
		}
	}

	public static final class MessageTextConverter implements JsonSerializer<MessageText> {
		public JsonElement serialize(final MessageText messageText, final Type srcType, final JsonSerializationContext context) {
			return new JsonPrimitive(messageText.getDisplay());
		}
	}

	public static final class ConstraintConverter implements JsonSerializer<Constraint> {
		public JsonElement serialize(final Constraint constraint, final Type srcType, final JsonSerializationContext context) {
			JsonObject jobject = new JsonObject();
			jobject.addProperty("name", constraint.getName());
			jobject.addProperty("errorMessage", constraint.getErrorMessage().getDisplay());
			jobject.addProperty(constraint.getProperty().getName(), constraint.getPropertyValue().toString());
			return jobject;
		}
	}

	public static final class DefinitionReferenceConverter implements JsonSerializer<DefinitionReference> {
		public JsonElement serialize(final DefinitionReference definitionReference, final Type srcType, final JsonSerializationContext context) {
			return new JsonPrimitive(definitionReference.get().getName());
		}
	}

	private static class FieldExclusion implements ExclusionStrategy {
		private final Class clazz;
		private final String fieldName;

		FieldExclusion(Class clazz, String fieldName) {
			this.clazz = clazz;
			this.fieldName = fieldName;
		}

		public boolean shouldSkipClass(Class<?> arg0) {
			return false;
		}

		public boolean shouldSkipField(FieldAttributes fieldAttributes) {
			return (clazz.equals(fieldAttributes.getDeclaringClass()) && fieldAttributes.getName().equals(fieldName));
		}
	}
}
