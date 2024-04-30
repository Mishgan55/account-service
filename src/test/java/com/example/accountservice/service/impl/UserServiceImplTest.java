package com.example.accountservice.service.impl;


import com.example.accountservice.dto.UserDto;
import com.example.accountservice.entity.User;
import com.example.accountservice.mapper.UserMapper;
import com.example.accountservice.repository.UserRepository;
import com.example.accountservice.utill.enums.DocumentType;
import com.example.accountservice.utill.user.UserDuplicateDocumentTypeAndNumberException;
import com.example.accountservice.utill.user.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserById_UserExists_ReturnsUserDto() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setDocumentNumber("1234");
        UserDto result = new UserDto();
        result.setDocumentNumber("1234");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(result);

        UserDto userDto = userService.getUserById(userId);

        assertNotNull(userDto);
        assertEquals(userDto.getDocumentNumber(), user.getDocumentNumber());
    }

    @Test
    void getUserById_UserDoesNotExist_ThrowsUserNotFoundException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getAllUsers_ReturnsListOfUserDto() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> userDtoList = userService.getAllUsers();

        assertNotNull(userDtoList);
    }

    @Test
    void createUser_ValidUserDto_CreatesUser() {

        String test = "test";
        UserDto userDto = new UserDto();
        userDto.setName(test);
        userDto.setDocumentNumber(test);
        userDto.setDocumentType(DocumentType.PASSPORT);

        User user = new User();
        user.setName(test);
        when(userRepository.findByDocumentNumberAndDocumentType(test, DocumentType.PASSPORT)).thenReturn(Optional.empty());
        when(userMapper.toUser(userDto)).thenReturn(user);
        userService.createUser(userDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_DuplicateDocument_ThrowsUserDuplicateDocumentTypeAndNumberException() {

        String test = "test";
        UserDto userDto = new UserDto();
        userDto.setName(test);
        userDto.setDocumentNumber(test);
        userDto.setDocumentType(DocumentType.PASSPORT);

        User user = new User();
        user.setName(test);
        when(userRepository.findByDocumentNumberAndDocumentType(test, DocumentType.PASSPORT)).thenReturn(Optional.of(user));

        assertThrows(UserDuplicateDocumentTypeAndNumberException.class, () -> userService.createUser(userDto));
    }

    @Test
    void deleteUser_UserExists_DeletesUser() {

        Long userId = 1L;

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void updateUser_UserExists_UpdatesUser() {

        Long userId = 1L;
        String name = "NewFirst";
        UserDto userUpdateModel = new UserDto();
        userUpdateModel.setName(name);

        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));


        userService.updateUser(userUpdateModel, userId);

        verify(userRepository, times(1)).findById(userId);
        assertEquals(name, user.getName());

    }

    @Test
    void updateUser_UserDoesNotExist_ThrowsUserNotFoundException() {

        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        UserDto userDto = new UserDto();

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userDto, userId));
    }
}
