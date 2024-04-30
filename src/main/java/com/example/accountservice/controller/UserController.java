package com.example.accountservice.controller;

import com.example.accountservice.dto.UserDto;
import com.example.accountservice.service.UserService;
import com.example.accountservice.utill.PropertyUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "User ID", required = true) @PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Successful operation", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
    })
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Add a new user")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @PostMapping("/add")
    public ResponseEntity<String> addUser(
            @Parameter(description = "User object to be added", required = true) @RequestBody @Validated UserDto userDto) {
        userService.createUser(userDto);
        return ResponseEntity.ok(PropertyUtil.ADD_SUCCESSFUL);
    }

    @Operation(summary = "Delete user by ID")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(
            @Parameter(description = "User ID", required = true) @PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(PropertyUtil.DELETE_SUCCESSFUL);
    }

    @Operation(summary = "Edit user by ID")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @PatchMapping("/{id}/edit")
    public ResponseEntity<String> editUser(
            @Parameter(description = "User ID", required = true) @PathVariable("id") Long id,
            @Parameter(description = "User object to be edited", required = true) @RequestBody @Validated UserDto userDto) {
        userService.updateUser(userDto, id);
        return ResponseEntity.ok(PropertyUtil.EDIT_SUCCESSFUL);
    }
}
