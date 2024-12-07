package com.softserve.itacademy.todolist.controller;

import com.softserve.itacademy.todolist.dto.ApiResponse;
import com.softserve.itacademy.todolist.dto.taskDto.TaskResponse;
import com.softserve.itacademy.todolist.dto.todoDto.TodoResponse;
import com.softserve.itacademy.todolist.dto.userDto.UserConverter;
import com.softserve.itacademy.todolist.dto.userDto.UserRequest;
import com.softserve.itacademy.todolist.dto.userDto.UserResponse;
import com.softserve.itacademy.todolist.model.ToDo;
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

    @GetMapping
    List<UserResponse> getAll() {
        return userService.getAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

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

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UserRequest userRequest, @PathVariable long id) {

        User user = userService.readById(id);
        User updatedUser = userConverter.fillUserDataFromRequest(userRequest);
        updatedUser.setId(user.getId());
        userService.update(updatedUser);
        return ResponseEntity.ok().body("User with ID: " + user.getId() + " was updated");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        userService.delete(id);
        return ResponseEntity.ok().body("User with ID: " + id + " was deleted");
    }

    @GetMapping("{user_id}/todos")
    public ResponseEntity<ApiResponse<List<TodoResponse>>> getAllTodoByUserId(@PathVariable("user_id") long id) {
        User user = userService.readById(id);

        List<TodoResponse> listOfTodo = user.getMyTodos().stream()
                .map(TodoResponse::new)
                .toList();

        return ResponseEntity.ok().body(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Retrieved all todos by user id " + id,
                listOfTodo
        ));
    }

    @GetMapping("{user_id}/todos/{todo_id}/tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAllTodoByUserId(
            @PathVariable("user_id") long user_id,
            @PathVariable("todo_id") long todo_id) {
        User user = userService.readById(user_id);

        List<ToDo> listOfTodo = user.getMyTodos().stream().toList();


        List<TaskResponse> tasks = listOfTodo.stream().flatMap(toDo -> toDo.getTasks().stream()
                .map(TaskResponse::new)).toList();
        if (tasks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "There are no tasks",
                    null
            ));
        }

        return ResponseEntity.ok().body(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Retrieved all tasks by user %d and todo %d ".formatted(user_id, todo_id),
                tasks
        ));
    }

}
