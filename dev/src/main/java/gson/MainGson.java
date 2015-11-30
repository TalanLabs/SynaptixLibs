package gson;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.IdRaw;

import model.ICountry;

public class MainGson {

	private static class TestService {
		
		public void toto(List<ICountry> countries) {
			System.out.println(countries);
		}
		
	}
	
	public static void main(String[] args) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Class.class, new ClassTypeAdapter());
		gsonBuilder.registerTypeHierarchyAdapter(IComponent.class,
				new ComponentTypeAdapter());
		gsonBuilder.registerTypeAdapter(Serializable.class,
				new SerializableTypeAdapter());
		Gson gson = gsonBuilder.create();

		ICountry country = ComponentFactory.getInstance().createInstance(
				ICountry.class);
		country.setId(new IdRaw("12"));
		country.setVersion(0);
		country.setCountry("FRA");
		country.setIsoCountryCode("FR");

		String s = gson.toJson(Arrays.asList(country));
		System.out.println(s);
		
		TestService testService = new TestService();
		
//		TestService.class.getMethod(name, parameterTypes);
		
		List<ICountry> cs = gson.fromJson(s, new TypeToken<List<ICountry>>() {
		}.getType());
		System.out.println(cs);

	}

	private static class ComponentTypeAdapter implements
			JsonSerializer<IComponent>, JsonDeserializer<IComponent> {

		@Override
		public JsonElement serialize(IComponent src, Type typeOfSrc,
				JsonSerializationContext context) {
			JsonObject componentObject = new JsonObject();
			componentObject.add("componentClass", context
					.serialize(ComponentFactory.getInstance()
							.getComponentClass(src)));

			JsonObject valuesObject = new JsonObject();
			for (Entry<String, Object> entry : src.straightGetProperties()
					.entrySet()) {
				valuesObject.add(
						entry.getKey(),
						context.serialize(entry.getValue(),
								src.straightGetPropertyClass(entry.getKey())));
			}
			componentObject.add("values", valuesObject);
			return componentObject;
		}

		@SuppressWarnings("unchecked")
		@Override
		public IComponent deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			JsonObject componentObject = json.getAsJsonObject();
			IComponent component = ComponentFactory.getInstance()
					.createInstance(
							(Class<? extends IComponent>) context.deserialize(
									componentObject.get("componentClass"),
									Class.class));
			JsonObject valuesObject = componentObject.getAsJsonObject("values");
			for (Entry<String, JsonElement> entry : valuesObject.entrySet()) {
				component.straightSetProperty(entry.getKey(), context
						.deserialize(entry.getValue(), component
								.straightGetPropertyClass(entry.getKey())));
			}
			return component;
		}
	}

	private static class SerializableTypeAdapter implements
			JsonSerializer<Serializable>, JsonDeserializer<Serializable> {

		@Override
		public JsonElement serialize(Serializable src, Type typeOfSrc,
				JsonSerializationContext context) {
			return new JsonPrimitive(((IdRaw) src).getHex());
		}

		@Override
		public Serializable deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			return new IdRaw(json.getAsString());
		}
	}

	private static class ClassTypeAdapter implements JsonSerializer<Class<?>>,
			JsonDeserializer<Class<?>> {

		public JsonElement serialize(Class<?> src, Type typeOfSrc,
				JsonSerializationContext context) {
			return new JsonPrimitive(src.getName());
		}

		@Override
		public Class<?> deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			try {
				return this.getClass().getClassLoader()
						.loadClass(json.getAsString());
			} catch (ClassNotFoundException e) {
				throw new JsonParseException(e);
			}
		}
	}
}
