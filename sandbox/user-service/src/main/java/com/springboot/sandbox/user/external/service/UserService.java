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
    public UserDto updateUser(String username, UserUpdateDto userUpdateDto) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new NotFoundException("User '%s' not found!".formatted(username)));

        if (!userUpdateDto.getEmail().equalsIgnoreCase(userEntity.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(userUpdateDto.getEmail(), userEntity.getId())) {
                throw new BadRequestException("User with email '%s' already exists!".formatted(userUpdateDto.getEmail()));
            }
            userEntity.setEmail(userUpdateDto.getEmail());
        } else {
            userEntity.setEmail(userEntity.getEmail());
        }

        String normalizedPhoneNumber = Formatter.normalizePhoneNumber(userUpdateDto.getPhoneNumber());
        if(!normalizedPhoneNumber.equals(userEntity.getPhoneNumber())){
            if(userRepository.existsByPhoneNumberAndIdNot(normalizedPhoneNumber, userEntity.getId())){
                throw new BadRequestException("User with phone number '%s' already exists!".formatted(normalizedPhoneNumber));
            }
            userEntity.setPhoneNumber(normalizedPhoneNumber);
        } else {
            userEntity.setPhoneNumber(userEntity.getPhoneNumber());
        }
        
        userEntity.setPassword(userEntity.getPassword());
        userEntity.setFullName(userUpdateDto.getFullName());
        userEntity.setDob(userUpdateDto.getDob());
        userEntity.setAddress(userUpdateDto.getAddress());
        userEntity.setStatus(userUpdateDto.getStatus());
        
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
