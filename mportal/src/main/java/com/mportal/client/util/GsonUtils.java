package com.mportal.client.util;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class GsonUtils {
	
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
}
