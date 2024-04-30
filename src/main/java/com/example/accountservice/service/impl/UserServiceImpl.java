package com.example.accountservice.service.impl;

import com.example.accountservice.dto.UserDto;
import com.example.accountservice.entity.User;
import com.example.accountservice.mapper.UserMapper;
import com.example.accountservice.repository.UserRepository;
import com.example.accountservice.service.UserService;
import com.example.accountservice.utill.PropertyUtil;
import com.example.accountservice.utill.user.UserDuplicateDocumentTypeAndNumberException;
import com.example.accountservice.utill.user.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto getUserById(Long id) {
        return userRepository
                .findById(id)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new UserNotFoundException(String.format(PropertyUtil.USER_NOT_FOUND, id), new Date()));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    @Transactional
    public void createUser(UserDto userDto) {
        validateUserUniqueness(userDto);
        userRepository.save(userMapper.toUser(userDto));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateUser(UserDto userDto, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(PropertyUtil.USER_NOT_FOUND, id), new Date()));

        user.setName(userDto.getName());
        user.setDocumentNumber(userDto.getDocumentNumber());
        user.setDocumentType(userDto.getDocumentType());
    }

    private void validateUserUniqueness(UserDto userDto) {
        isUserDocumentUnique(userDto);
    }

    private void isUserDocumentUnique(UserDto userDto) {
        if (userRepository.findByDocumentNumberAndDocumentType(userDto.getDocumentNumber(), userDto.getDocumentType())
                .isPresent()) {
            throw new UserDuplicateDocumentTypeAndNumberException(PropertyUtil.DUPLICATE_USER_DOCUMENT, new Date());
        }
    }
}
