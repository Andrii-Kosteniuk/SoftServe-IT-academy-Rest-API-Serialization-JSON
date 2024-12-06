package com.softserve.itacademy.todolist.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
    private int status;
    private T body;

    public ApiResponse(int status, T body) {
        this.status = status;
        this.body = body;
    }

}
