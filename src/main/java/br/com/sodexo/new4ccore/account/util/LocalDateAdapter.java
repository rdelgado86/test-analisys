package br.com.sodexo.new4ccore.account.util;

import java.lang.reflect.Type;
import java.time.LocalDate;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import br.com.sodexo.new4ccore.account.constants.Constants;

public class LocalDateAdapter implements JsonSerializer<LocalDate> {

	public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(date.format(Constants.DEFAULT_DATE_FORMATTER));
	}
	
}