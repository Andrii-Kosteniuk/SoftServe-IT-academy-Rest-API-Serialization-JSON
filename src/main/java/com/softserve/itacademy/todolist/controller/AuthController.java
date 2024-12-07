package com.softserve.itacademy.todolist.controller;

import com.softserve.itacademy.todolist.dto.userDto.UserRequest;
import com.softserve.itacademy.todolist.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest request) {
        UserDetails userDetails = userService.loadUserByUsername(request.email());

        if (userDetails == null || ! passwordEncoder.matches(request.password(), userDetails.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid email or password");
        }

        return ResponseEntity.ok().body("Authentication successful");

    }

}
