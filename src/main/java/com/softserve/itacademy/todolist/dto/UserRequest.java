package com.softserve.itacademy.todolist.dto;


public record UserRequest(String firstName, String lastName, String email, String password, String role) {
}
