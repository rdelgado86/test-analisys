package br.com.sodexo.new4ccore.account.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

import br.com.sodexo.new4ccore.account.constants.Constants;

public class DateUtils {
	
    public static LocalDateTime getCurrentDateTime(){
    	return LocalDateTime.now(ZoneId.of(Constants.TAG_ZONE_ID_AMERICA_SP));
    }
	
}