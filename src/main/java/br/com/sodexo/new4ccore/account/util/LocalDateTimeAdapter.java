package br.com.sodexo.new4ccore.account.util;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import br.com.sodexo.new4ccore.account.constants.Constants;

public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime> {

	public JsonElement serialize(LocalDateTime dateTime, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(dateTime.format(Constants.DEFAULT_DATETIME_FORMATTER));
	}
	
}