package com.softserve.itacademy.todolist.dto.userDto;


public record UserRequest(String firstName, String lastName, String email, String password, String role) {
}
