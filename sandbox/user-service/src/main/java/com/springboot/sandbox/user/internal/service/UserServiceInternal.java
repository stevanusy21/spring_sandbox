package com.springboot.sandbox.user.internal.service;

import org.springframework.stereotype.Service;

import com.springboot.sandbox.common.exception.NotFoundException;
import com.springboot.sandbox.user.entity.UserEntity;
import com.springboot.sandbox.user.internal.dto.response.UserAuthDto;
import com.springboot.sandbox.user.repository.UserRepository; 
import com.springboot.sandbox.user.util.UserMapper; 
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j
@RequiredArgsConstructor 
public class UserServiceInternal {
    private final UserRepository userRepository; 
    private final UserMapper mapper; 

    public UserAuthDto findUserAuthByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username) 
                .orElseThrow(() -> new NotFoundException("User '%s' not found!".formatted(username)));
        return mapper.toUserAuthDto(userEntity);
    }
}
