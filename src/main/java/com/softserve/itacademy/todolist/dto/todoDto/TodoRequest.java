package com.softserve.itacademy.todolist.dto.todoDto;

import com.softserve.itacademy.todolist.model.User;


public record TodoRequest(String title, User owner) {
}
