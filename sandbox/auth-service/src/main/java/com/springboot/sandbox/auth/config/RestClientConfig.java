package com.springboot.sandbox.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @LoadBalanced 
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
    
    @Bean 
    public RestClient userServiceRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl("http://user-service")
                .build();
    }
}
