package com.example.accountservice.service;

import com.example.accountservice.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();

    UserDto createUser(UserDto userDto);

    void deleteUser(Long id);

    UserDto updateUser(UserDto userDto, Long id);
}
