package br.com.sodexo.new4ccore.account.constants;

import java.time.format.DateTimeFormatter;

public class Constants {
	
	public static final Long DEFAULT_GLOBAL_REQUEST_TIMEOUT = 30000L;
	public static final Long BAAS_PARTNER_REQUEST_TIMEOUT = 30000L;
	
	public static final String REQUEST_UUID_HEADER = "requestUUID";
	public static final String AUTHORIZATION = "Authorization";
	public static final String APIGW = "x-apigw-api-id";

	public static final String DATE_FORMAT_DATETIME= "yyyy-MM-dd'T'HH:mm:ss";
	public static final String DATE_FORMAT_DATETIME_ZONE= "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	public static final String TAG_ZONE_ID_AMERICA_SP = "America/Sao_Paulo";
	public static final String DOCK_ERROR_GENERATE_TOKEN = "Couldn't generate access token from BAAS Partner - DOCK";

	public static final String UNABLE_TO_SEND_REQUEST = "Unable to send request.";
	
	public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	public static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);

	public static final String PROBE_LOG = "/actuator/health";
}
