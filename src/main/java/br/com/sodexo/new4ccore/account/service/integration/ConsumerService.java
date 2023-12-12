package br.com.sodexo.new4ccore.account.service.integration;


import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.sodexo.new4ccore.account.dto.response.ConsumerResponseDTO;
import br.com.sodexo.new4ccore.account.enums.ErrorsEnum;
import br.com.sodexo.new4ccore.account.exception.AccountException;
import br.com.sodexo.new4ccore.account.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConsumerService {

    @Value("${multibenefits.consumer.base_path}")
    private String basepath;

    @Autowired
    private RestTemplate restTemplate;
	
	public ResponseEntity<ConsumerResponseDTO> getConsumer(Long consumerId) throws AccountException {
        try {
			String url = UriComponentsBuilder.fromHttpUrl(basepath).path(consumerId.toString()).toUriString();
			log.info("URL Consumer: {}", url);
			HttpEntity<String> requestEntity = HttpUtils.buildHeader();
            ResponseEntity<ConsumerResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    ConsumerResponseDTO.class
            );
            log.info("Consumer Response: {}", response);
            return response;
        }
        catch (Exception ex) {
            if (ex instanceof HttpClientErrorException error) {
                if (error.getStatusCode() == HttpStatus.NOT_FOUND) {
                	throw ErrorsEnum.CONSUMER_NOT_FOUND.newError();
                }
            }
            log.error("Error: {} ", ex.getMessage());
            throw ErrorsEnum.INTERNAL_SERVER_ERROR.newError();
        }
	}
	
    @Async
    public Future<Boolean> consumerExistsAsync(Long consumerId) throws AccountException {
    	return AsyncResult.forValue(this.getConsumer(consumerId).getStatusCode()==HttpStatus.OK);
    }

}
