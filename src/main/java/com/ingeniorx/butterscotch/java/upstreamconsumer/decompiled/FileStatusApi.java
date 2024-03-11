package com.ingeniorx.butterscotch.java.upstreamconsumer.decompiled;

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
public class FileStatusApi {

    private RestTemplate restTemplate;

    private String fileStatusServiceBaseUrl;

    private static final String FILE_STATUS_SERVICE_BASE_URL = "/fileStatus";

    public FileStatusApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public FileStatusApi(RestTemplate restTemplate, String fileStatusServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.fileStatusServiceBaseUrl = fileStatusServiceBaseUrl;
    }

    public FileStatusDto save(FileStatusDto fileStatusDto, String token){
        log.debug("Making request to save filestatusdto");

        String url = UriComponentsBuilder.fromHttpUrl(fileStatusServiceBaseUrl)
                .toUriString();

        HttpEntity<FileStatusDto> entity = new HttpEntity<>(fileStatusDto,getAuthorization(token));
        return restTemplate.exchange(url,
                HttpMethod.POST,
                entity,
                FileStatusDto.class)
                .getBody();
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
