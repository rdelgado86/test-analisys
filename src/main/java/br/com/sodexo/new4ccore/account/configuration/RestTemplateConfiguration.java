package br.com.sodexo.new4ccore.account.configuration;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import br.com.sodexo.new4ccore.account.constants.Constants;

@Configuration
public class RestTemplateConfiguration {
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplateBuilder()
				.setConnectTimeout(Duration.ofMillis(Constants.DEFAULT_GLOBAL_REQUEST_TIMEOUT))
				.setReadTimeout(Duration.ofMillis(Constants.DEFAULT_GLOBAL_REQUEST_TIMEOUT))
				.build();
	}
    
}