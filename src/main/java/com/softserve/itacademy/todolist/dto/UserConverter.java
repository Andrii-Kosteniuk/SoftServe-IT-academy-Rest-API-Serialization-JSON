package com.softserve.itacademy.todolist.dto;

import com.softserve.itacademy.todolist.model.Role;
import com.softserve.itacademy.todolist.model.User;
import com.softserve.itacademy.todolist.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserConverter {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public User fillUserDataFromRequest(UserRequest request) {
        User user = new User();

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        Role role = roleRepository.findByName(request.role());
        if (role == null) {
            throw new EntityNotFoundException("Role not found");
        }
        user.setRole(role);
        return user;
    }
}
