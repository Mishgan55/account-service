package com.example.accountservice.controller;

import com.example.accountservice.dto.UserDto;
import com.example.accountservice.service.UserService;
import com.example.accountservice.utill.PropertyUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get user by ID")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(
            @Parameter(description = "User ID", required = true) @PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Add a new user")
    @ApiResponse(responseCode = "201", description = "Successful operation")
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(
            @Parameter(description = "User object to be added", required = true) @RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }

    @Operation(summary = "Delete user by ID")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteUserById(
            @Parameter(description = "User ID", required = true) @PathVariable("id") Long id) {
        userService.deleteUser(id);
        return PropertyUtil.DELETE_SUCCESSFUL;
    }

    @Operation(summary = "Edit user by ID")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @PatchMapping("/{id}/edit")
    @ResponseStatus(HttpStatus.OK)
    public UserDto editUser(
            @Parameter(description = "User ID", required = true) @PathVariable("id") Long id,
            @Parameter(description = "User object to be edited", required = true) @RequestBody @Valid UserDto userDto) {
        return userService.updateUser(userDto, id);
    }
}
