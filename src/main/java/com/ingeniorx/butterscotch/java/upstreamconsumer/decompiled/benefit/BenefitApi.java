package com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled.benefit;

import com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled.BenefitDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class BenefitApi {

    private RestTemplate restTemplate;

    private String benefitServiceBaseUrl;

    public BenefitApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BenefitApi(RestTemplate restTemplate, String benefitServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.benefitServiceBaseUrl = benefitServiceBaseUrl;
    }



    public BenefitDto getByBenefitVersionId (Long benefitVersionId, boolean fullProcess, String token) {
        log.debug("Making request to check for {}", benefitVersionId);
        String url = UriComponentsBuilder.fromHttpUrl(benefitServiceBaseUrl)
                .path("/benefit/version/{benefitVersionId}")
                .queryParam( "fullProcess", fullProcess)
                .buildAndExpand (benefitVersionId)
                .toUriString();
        HttpEntity<Void> entity = new HttpEntity<>(getAuthorization(token));
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                BenefitDto.class).getBody();
    }

    public BenefitDto get(Long id,String token) {
        String url = UriComponentsBuilder.fromHttpUrl(benefitServiceBaseUrl)
                .path("/benefit/{id}")
                .buildAndExpand (id)
                .toUriString();
        HttpEntity<Void> entity = new HttpEntity<>(getAuthorization(token));
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                BenefitDto.class).getBody();
    }

    public HttpHeaders getAuthorization(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(token != null){
            headers.set("Authorization",token.startsWith("Bearer") ? token : "Bearer " + token);
        }
        return headers;
    }
}
