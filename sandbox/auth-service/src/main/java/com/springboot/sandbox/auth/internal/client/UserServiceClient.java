package com.springboot.sandbox.auth.internal.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.springboot.sandbox.auth.internal.dto.UserAuthDto;
import com.springboot.sandbox.common.dto.ApiResponse;
import com.springboot.sandbox.common.exception.NotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {
    private final RestClient userServiceRestClient;

    public UserAuthDto findUserForAuth(String username) {
        log.info("Fetching user credentials for username: {} from user-service", username);
        
        ApiResponse<UserAuthDto> response = userServiceRestClient.get()
                                                .uri("/api/internal/users/{username}", username)
                                                .retrieve()
                                                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                                                    if(res.getStatusCode() == HttpStatus.NOT_FOUND) {
                                                        throw new NotFoundException("User '%s' not found".formatted(username));
                                                    }
                                                })
                                                .body(new ParameterizedTypeReference<ApiResponse<UserAuthDto>>(){});

        return response != null ? response.getData() : null;
    }
}
