package br.com.sodexo.new4ccore.account.service.integration;


import br.com.sodexo.new4ccore.account.dto.response.CustomerResponseDTO;
import br.com.sodexo.new4ccore.account.enums.ErrorsEnum;
import br.com.sodexo.new4ccore.account.exception.AccountException;
import br.com.sodexo.new4ccore.account.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
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

import java.util.concurrent.Future;

@Service
@Slf4j
public class CustomerService {

    @Value("${multibenefits.customer.base_path}")
    private String basepath;

    @Autowired
    private RestTemplate restTemplate;

	public ResponseEntity<CustomerResponseDTO> getCustomer(Long customerId) throws AccountException {
        try {
			String url = UriComponentsBuilder.fromHttpUrl(basepath).path(customerId.toString()).toUriString();
			log.info("URL Customer: " + url);
			HttpEntity<String> requestEntity = HttpUtils.buildHeader();
            ResponseEntity<CustomerResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    CustomerResponseDTO.class
            );
            log.info("Customer Response: " + response);
            return response;
        }
        catch (Exception ex) {
            if (ex instanceof HttpClientErrorException error) {
                if (error.getStatusCode() == HttpStatus.NOT_FOUND) {
                	throw ErrorsEnum.CUSTOMER_NOT_FOUND.newError();
                }
            }
            log.error("Error: " + ex.getMessage());
            throw ErrorsEnum.INTERNAL_SERVER_ERROR.newError();
        }
	}

    @Async
    public Future<Boolean> customerExistsAsync(Long customerId) throws AccountException {
    	return AsyncResult.forValue(this.getCustomer(customerId).getStatusCode()==HttpStatus.OK);
    }

}
