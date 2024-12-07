package com.softserve.itacademy.todolist.controller;

import com.softserve.itacademy.todolist.dto.ApiResponse;
import com.softserve.itacademy.todolist.dto.userDto.UserConverter;
import com.softserve.itacademy.todolist.dto.userDto.UserRequest;
import com.softserve.itacademy.todolist.dto.userDto.UserResponse;
import com.softserve.itacademy.todolist.model.User;
import com.softserve.itacademy.todolist.repository.UserRepository;
import com.softserve.itacademy.todolist.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserConverter userConverter;

    // Get all users
    @GetMapping
    List<UserResponse> getAll() {
        return userService.getAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    // Get user by specific ID
    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") int id) {

        User user = userService.readById(id);
        UserResponse userResponse = new UserResponse(user);
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Retrieved user",
                userResponse);

        return ResponseEntity.ok(apiResponse);

    }

    // Create new user
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.email()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
        }

        User user = userService.create(userRequest);
        UserResponse response = new UserResponse(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "New user was created",
                response));
    }

    // Update existing user
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UserRequest userRequest, @PathVariable long id) {

        User user = userService.readById(id);
        User updatedUser = userConverter.fillUserDataFromRequest(userRequest);
        updatedUser.setId(user.getId());
        userService.update(updatedUser);
        return ResponseEntity.ok().body("User with ID: " + user.getId() + " was updated");
    }

    // Delete user by specific ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        userService.delete(id);
        return ResponseEntity.ok().body("User with ID: " + id + " was deleted");
    }

}
