package com.softserve.itacademy.todolist.service;

import com.softserve.itacademy.todolist.dto.UserRequest;
import com.softserve.itacademy.todolist.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User create(UserRequest user);
    User readById(long id);
    User update(User user);
    void delete(long id);
    List<User> getAll();
}
