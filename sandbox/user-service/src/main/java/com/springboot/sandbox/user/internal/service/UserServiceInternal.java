package com.springboot.sandbox.user.internal.service;

import org.springframework.stereotype.Service;

import com.springboot.sandbox.common.enumeration.AccountStatus;
import com.springboot.sandbox.common.exception.BadRequestException;
import com.springboot.sandbox.common.exception.NotFoundException;
import com.springboot.sandbox.common.util.Formatter;
import com.springboot.sandbox.user.entity.UserEntity;
import com.springboot.sandbox.user.internal.dto.request.UserCreateDto;
import com.springboot.sandbox.user.internal.dto.request.UserResetPasswordDto;
import com.springboot.sandbox.user.internal.dto.response.UserAuthDto;
import com.springboot.sandbox.user.internal.dto.response.UserDetailDto;
import com.springboot.sandbox.user.internal.dto.response.UserEmailDto;
import com.springboot.sandbox.user.repository.UserRepository; 
import com.springboot.sandbox.user.util.UserMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j
@RequiredArgsConstructor 
public class UserServiceInternal {
    private final UserRepository userRepository; 
    private final UserMapper mapper; 

    @Transactional
    public UserEmailDto createUser(UserCreateDto userRequestDto) {
        if (userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new BadRequestException("User with username '%s' already exists!".formatted(userRequestDto.getUsername()));
        }
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new BadRequestException("User with email '%s' already exists!".formatted(userRequestDto.getEmail()));
        }

        String normalizedPhoneNumber = Formatter.normalizePhoneNumber(userRequestDto.getPhoneNumber());
        if (userRepository.existsByPhoneNumber(normalizedPhoneNumber)) {
            throw new BadRequestException("User with phone number '%s' already exists!".formatted(userRequestDto.getPhoneNumber()));
        }

        UserEntity userEntity = mapper.toUserEntity(userRequestDto);
        userEntity.setPhoneNumber(normalizedPhoneNumber);
        userEntity.setStatus(AccountStatus.ACTIVE);

        UserEntity savedUser = userRepository.save(userEntity);

        return mapper.toUserEmailDto(savedUser);
    }

    @Transactional 
    public UserEmailDto resetPassword(String username, UserResetPasswordDto request) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new NotFoundException("User '%s' not found!".formatted(username)));
        
        userEntity.setPassword(request.getPassword());

        UserEntity savedUser = userRepository.save(userEntity);

        return mapper.toUserEmailDto(savedUser);
    }   

    public UserAuthDto findUserAuthByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username) 
                .orElseThrow(() -> new NotFoundException("User '%s' not found!".formatted(username)));
        return mapper.toUserAuthDto(userEntity);
    }

    public UserDetailDto findUserDetailById(Long id) {
        UserEntity userEntity = userRepository.findById(id) 
                .orElseThrow(() -> new NotFoundException("User '%s' not found!".formatted(id)));
        return mapper.toUserDetailDto(userEntity);
    }
}
