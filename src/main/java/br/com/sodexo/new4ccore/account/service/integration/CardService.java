package br.com.sodexo.new4ccore.account.service.integration;

import br.com.sodexo.new4ccore.account.dto.request.CancelCardRequestDTO;
import br.com.sodexo.new4ccore.account.dto.response.CardDTO;
import br.com.sodexo.new4ccore.account.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class CardService {

    @Value("${multibenefits.card.base_path}")
    private String basepath;

    @Autowired
    private RestTemplate restTemplate;

    public List<CardDTO> getCardsByAccountId(Long accountId) {

        var urlBuilder = UriComponentsBuilder.fromUriString(basepath + "accounts/{accountId}/cards");
        var url = urlBuilder.buildAndExpand(Collections.singletonMap("accountId", accountId.toString())).toString();
        log.info("URL getCardsByAccountId: {}", url);

        var requestEntity = HttpUtils.buildHeader();
        var response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<CardDTO>>() {}
        );
        log.info("Response getCardsByAccountId: {}", response);
        return response.getBody();
    }

    public void cancelCard(Long cardId, CancelCardRequestDTO request) {

        var urlBuilder = UriComponentsBuilder.fromUriString(basepath + "cards/{cardId}/cancel");
        var url = urlBuilder.buildAndExpand(Collections.singletonMap("cardId", cardId.toString())).toString();
        log.info("URL cancelCard: {}", url);

        var requestEntity = HttpUtils.buildHeader(request);
        var response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        log.info("Response cancelCard: {}", response);
    }

}
