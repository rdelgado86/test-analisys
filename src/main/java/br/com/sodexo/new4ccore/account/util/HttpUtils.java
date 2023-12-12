package br.com.sodexo.new4ccore.account.util;

import br.com.sodexo.new4ccore.account.constants.Constants;
import br.com.sodexo.new4ccore.account.interceptor.LoggerInterceptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class HttpUtils {

    public static HttpEntity<String> buildHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(Constants.REQUEST_UUID_HEADER, LoggerInterceptor.getUuid());
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, headers);
        return requestEntity;
    }

    public static <T> HttpEntity<T> buildHeader(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(Constants.REQUEST_UUID_HEADER, LoggerInterceptor.getUuid());
        return new HttpEntity<T>(body, headers);
    }
}
