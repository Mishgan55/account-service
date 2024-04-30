package com.example.accountservice.mapper;

import com.example.accountservice.dto.UserDto;
import com.example.accountservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "name", target = "name")
    UserDto toUserDto(User user);

    User toUser(UserDto userDto);
}
