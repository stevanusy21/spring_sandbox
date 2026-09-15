package com.springboot.sandbox.user.util;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.springboot.sandbox.user.external.dto.request.UserCreateDto;
import com.springboot.sandbox.user.external.dto.response.UserDto;
import com.springboot.sandbox.user.internal.dto.response.UserAuthDto;
import com.springboot.sandbox.user.internal.dto.response.UserDetailDto;
import com.springboot.sandbox.user.entity.UserEntity;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE

)
public interface UserMapper {
    UserDto toUserDto(UserEntity userEntity);
    UserAuthDto toUserAuthDto(UserEntity userEntity);
    UserEntity toUserEntity(UserDto userDto);
    UserEntity toUserEntity(UserCreateDto userRequestDto);
    UserDetailDto toUserDetailDto(UserEntity userEntity);
}
