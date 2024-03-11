package com.ingeniorx.butterscotch.java.upstreamconsumer.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class IntegrationConfig {

    private String awsS3Endpoint;

    private String awsRegion;

    @Bean(name="benefitIntegrationModelMapper")
    public ModelMapper modelMapper(){
      ModelMapper modelMapper = new ModelMapper();
      return modelMapper;
    }

    @Bean
    public AmazonS3 amazonS3(){
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsS3Endpoint,awsRegion))
                .withPathStyleAccessEnabled(true)
                .build();
    }

    @Bean("integration_restTemplate")
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

}
