package com.springboot.sandbox.user.external.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.springboot.sandbox.common.dto.PageResponse;
import com.springboot.sandbox.common.enumeration.AccountStatus;
import com.springboot.sandbox.common.exception.BadRequestException;
import com.springboot.sandbox.common.exception.InternalServerException;
import com.springboot.sandbox.common.exception.NotFoundException;
import com.springboot.sandbox.common.util.AuditorUtil;
import com.springboot.sandbox.common.util.Formatter;
import com.springboot.sandbox.user.external.dto.request.UserCreateDto;
import com.springboot.sandbox.user.external.dto.request.UserUpdateDto;
import com.springboot.sandbox.user.external.dto.response.UserDto;
import com.springboot.sandbox.user.entity.UserEntity;
import com.springboot.sandbox.user.repository.UserRepository;
import com.springboot.sandbox.user.util.UserMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    public UserDto findUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User '%s' not found!".formatted(username)));
        return mapper.toUserDto(userEntity);
    }

    public PageResponse<UserDto> findUsersWithPaging(int page, int pageSize, String sortBy, Direction sortDirection) {
        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by(sortDirection, sortBy));
        Page<UserDto> userPage = userRepository.findAll(pageable).map(mapper::toUserDto);
        return PageResponse.from(userPage);
    }

    @Transactional
    public UserDto createUser(UserCreateDto userRequestDto) {
        if (userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new BadRequestException("User with username '%s' already exists!".formatted(userRequestDto.getUsername()));
        }
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new BadRequestException("User with email '%s' already exists!".formatted(userRequestDto.getEmail()));
        }
        if (userRepository.existsByPhoneNumber(userRequestDto.getPhoneNumber())) {
            throw new BadRequestException("User with phone number '%s' already exists!".formatted(userRequestDto.getPhoneNumber()));
        }

        String normalizedPhoneNumber = Formatter.normalizePhoneNumber(userRequestDto.getPhoneNumber());
        
        try {
            UserEntity userEntity = mapper.toUserEntity(userRequestDto);
            userEntity.setPhoneNumber(normalizedPhoneNumber);
            userEntity.setStatus(AccountStatus.ACTIVE);
            return mapper.toUserDto(userRepository.save(userEntity));
        } catch (Exception e) {
            log.error("Failed to create user", e);
            throw new InternalServerException("Failed to create user");
        }
    }

    @Transactional
    public UserDto updateUser(UserUpdateDto userUpdateDto) {
        UserEntity userEntity = userRepository.findByUsername(userUpdateDto.getUsername())
                .orElseThrow(
                        () -> new NotFoundException("User '%s' not found!".formatted(userUpdateDto.getUsername())));

        if (userUpdateDto.getPassword() != null) {
            userEntity.setPassword(userUpdateDto.getPassword());
        }

        if (userUpdateDto.getFullName() != null) {
            userEntity.setFullName(userUpdateDto.getFullName());
        }

        if (userUpdateDto.getDob() != null) {
            userEntity.setDob(userUpdateDto.getDob());
        }
        if (userUpdateDto.getEmail() != null && 
            !userUpdateDto.getEmail().equalsIgnoreCase(userEntity.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(userUpdateDto.getEmail(), userEntity.getId())) {
                throw new BadRequestException("User with email '%s' already exists!".formatted(userUpdateDto.getEmail()));
            }
            userEntity.setEmail(userUpdateDto.getEmail());
        }
        if (userUpdateDto.getPhoneNumber() != null) {
            String normalizedPhoneNumber = Formatter.normalizePhoneNumber(userUpdateDto.getPhoneNumber());
            if(!normalizedPhoneNumber.equals(userEntity.getPhoneNumber())){
                if(userRepository.existsByPhoneNumberAndIdNot(normalizedPhoneNumber, userEntity.getId())){
                    throw new BadRequestException("User with phone number '%s' already exists!".formatted(normalizedPhoneNumber));
                }
                userEntity.setPhoneNumber(normalizedPhoneNumber);
            }
        }
        if (userUpdateDto.getAddress() != null) {
            userEntity.setAddress(userUpdateDto.getAddress());
        }
        if (userUpdateDto.getStatus() != null) {
            userEntity.setStatus(userUpdateDto.getStatus());
        }
        return mapper.toUserDto(userRepository.save(userEntity));
    }

    @Transactional
    public Boolean deleteUser(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User '%s' not found!".formatted(username)));
        try {
            String currentUser = AuditorUtil.getCurrentUser();
            userEntity.markDeleted(currentUser);
            userEntity.setStatus(AccountStatus.DELETED);
            userRepository.save(userEntity);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete user", e);
            throw new InternalServerException("Failed to delete user");
        }
    }
}
