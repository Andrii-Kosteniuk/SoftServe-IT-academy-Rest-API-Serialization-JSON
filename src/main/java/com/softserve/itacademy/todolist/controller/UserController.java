package com.softserve.itacademy.todolist.controller;

import com.softserve.itacademy.todolist.dto.ApiResponse;
import com.softserve.itacademy.todolist.dto.UserResponse;
import com.softserve.itacademy.todolist.model.User;
import com.softserve.itacademy.todolist.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    List<UserResponse> getAll() {
        return userService.getAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") int id) {
        try {
            User user = userService.readById(id);
            UserResponse userResponse = new UserResponse(user);
            ApiResponse<UserResponse> apiResponse = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    userResponse);

            return ResponseEntity.ok(apiResponse);

        } catch (EntityNotFoundException e) {
            ApiResponse<UserResponse> notFoundUser = new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundUser);
        }

    }

}
