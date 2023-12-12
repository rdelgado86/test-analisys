package br.com.sodexo.new4ccore.account.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.MappedInterceptor;

import br.com.sodexo.new4ccore.account.interceptor.LoggerInterceptor;

@Configuration
public class LoggerConfiguration {
	
	@Bean("AccountLogger")
	public MappedInterceptor interceptor() {
		return new MappedInterceptor(null, new LoggerInterceptor());
	}
    
}