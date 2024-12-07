package com.softserve.itacademy.todolist.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String message;
    private T body;

    public ApiResponse(int status, String message, T body) {
        this.status = status;
        this.message = message;
        this.body = body;
    }

}
