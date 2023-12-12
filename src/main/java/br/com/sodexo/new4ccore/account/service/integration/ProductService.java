package br.com.sodexo.new4ccore.account.service.integration;

import br.com.sodexo.new4ccore.account.dto.response.ProductResponseDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class ProductService {

    @Value("${multibenefits.product.base_path}")
    private String basepath;

    @Autowired
    private RestTemplate restTemplate;

    public ProductResponseDTO getProduct(Long productId) throws AccountException {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(basepath).path(productId.toString()).toUriString();
            log.info("URL Customer: " + url);
            HttpEntity<String> requestEntity = HttpUtils.buildHeader();
            ResponseEntity<ProductResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    ProductResponseDTO.class
            );
            log.info("Product Response: " + response);
            return response.getBody();
        }
        catch (Exception ex) {
            if (ex instanceof HttpClientErrorException error) {
                if (error.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw ErrorsEnum.PRODUCT_NOT_FOUND.newError();
                }
            }
            log.error("Error: " + ex.getMessage());
            throw ErrorsEnum.INTERNAL_SERVER_ERROR.newError();
        }
    }

}
