package com.apppubs.d20.util;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.apppubs.ikidou.reflect.TypeBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
	
	private static Gson gson;
	static {

		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

			@Override
			public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
					throws JsonParseException {
				try {
					String jsonStr = json.getAsString();
					if (jsonStr == null || jsonStr.equals("")) {
						return null;
					} else {
						return df.parse(jsonStr);
					}
				} catch (ParseException e) {
					e.printStackTrace();
					return null;
				}
			}
		});

		gb.registerTypeAdapter(Integer.class, new JsonDeserializer<Integer>() {

			@Override
			public Integer deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
					throws JsonParseException {
				String jsonStr = json.getAsString();
				if (jsonStr == null || jsonStr.equals("")) {
					return 0;
				} else {
					return Integer.parseInt(jsonStr);
				}
			}
		});
		gson = gb.create();
	}
	
	public static Gson getGson(){
		return gson;
	}

	public static <T> T parseObjectFromJson(String jsonStr, Class<T> clazz){
		return gson.fromJson(jsonStr,clazz);
	}

	public static <T>List<T> parseListFromJson(String jsonStr,Class<T> clazz){
		Type type = TypeBuilder
				.newInstance(List.class)
				.beginSubType(clazz)
				.endSubType()
				.build();

		return gson.fromJson(jsonStr,type);
	}

	/**
	 * json转换成map
	 *
	 * @param json
	 * @return
	 */
	public static Map<String, Object> parseJSON2ObjectMap(String json) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (gson != null) {
			Type type = new TypeToken<Map<String, Object>>() {
			}.getType();
			map = gson.fromJson(json, type);
		}
		return map;
	}

	public static Map<String, String> parseJSON2StringMap(String json) {
		Map<String, String> map = null;
		if (gson != null) {
			Type type = new TypeToken<Map<String, String>>() {
			}.getType();
			map = gson.fromJson(json, type);
		}
		return map;
	}

	public static JSONObject parseJSONObject(String json) throws JSONException {
		JSONObject jo = new JSONObject(json);
		return jo;
	}
}
