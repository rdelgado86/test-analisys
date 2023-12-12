package br.com.sodexo.new4ccore.account.interceptor;

import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.sodexo.new4ccore.account.constants.Constants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class LoggerInterceptor implements HandlerInterceptor {
	
	public static final String START_TIME_MDC_KEY = "MBPLogger.startTime";
    public static final String REQUEST_UUID_MDC_KEY = "MBPLogger.requestUUID";
    
    public static String REQUEST_UUID = null;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    	REQUEST_UUID = StringUtils.isNotBlank(request.getHeader(Constants.REQUEST_UUID_HEADER)) ? request.getHeader(Constants.REQUEST_UUID_HEADER) : UUID.randomUUID().toString();
        MDC.put(REQUEST_UUID_MDC_KEY, REQUEST_UUID);
        MDC.put(START_TIME_MDC_KEY, ""+System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception exception) {
        Object attribute = ((HttpServletRequest) request).getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (Objects.nonNull(attribute) && ((String) attribute).equals(Constants.PROBE_LOG)) {
            return;
        }
        
        long executionTime = System.currentTimeMillis() - Long.parseLong(MDC.get(START_TIME_MDC_KEY));
        log.info("{} {} - took {} ms",  ((HttpServletRequest) request).getMethod(), attribute, executionTime);
        if (Objects.nonNull(exception)) {
            log.error("{}",exception);
        }
    }

    public static String getUuid(){
        return MDC.get(REQUEST_UUID_MDC_KEY);
    }

    public void onFinish() {
        MDC.clear();
    }
}