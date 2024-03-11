package com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled;


import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Component
public class DynamicLayerApi {

    private static final String DYNAMIC_LAYER_API_BASE_URL = "/dynamic-layer";
    private RestTemplate restTemplate;
    private String dynamicLayerServiceUrl;

    public List<DynamicLayerDto> getDynamicLayerByLayerNameOrDescription(String layerName, String layerDescription, String token) {
        String url = UriComponentsBuilder.fromHttpUrl(dynamicLayerServiceUrl)
                .path("/dynamicLayerByLayerNameOrDescription")
                .queryParam("layerName", layerName)
                .queryParam("layerDescription", layerDescription)
                .buildAndExpand()
                .toUriString();

        HttpEntity<Void> entity = new HttpEntity(getAuthorization(token));

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<DynamicLayerDto>>() {
                }
        ).getBody();
    }

    private HttpHeaders getAuthorization(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(token != null){
            headers.set("Authorization",token.startsWith("Bearer") ? token : "Bearer " + token);
        }
        return headers;
    }
}
