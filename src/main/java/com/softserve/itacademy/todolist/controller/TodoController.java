package com.softserve.itacademy.todolist.controller;

import com.softserve.itacademy.todolist.config.Auth;
import com.softserve.itacademy.todolist.dto.ApiResponse;
import com.softserve.itacademy.todolist.dto.todoDto.TodoRequest;
import com.softserve.itacademy.todolist.dto.todoDto.TodoResponse;
import com.softserve.itacademy.todolist.dto.userDto.CollaboratorRequest;
import com.softserve.itacademy.todolist.dto.userDto.UserResponse;
import com.softserve.itacademy.todolist.model.Task;
import com.softserve.itacademy.todolist.model.ToDo;
import com.softserve.itacademy.todolist.model.User;
import com.softserve.itacademy.todolist.service.ToDoService;
import com.softserve.itacademy.todolist.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final ToDoService toDoService;
    private final UserService userService;
    private final Auth auth;

    // Get all todos by user_id
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



    // Get all collaborators by todo_id
    @GetMapping("/{todo_id}/collaborators")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllCollaboratorsByTodoId(
            @PathVariable("todo_id") long todo_id) {

        ToDo toDo = toDoService.readById(todo_id);

        List<UserResponse> collaborators = toDo.getCollaborators().stream()
                .map(UserResponse::new)
                .toList();

        return ResponseEntity.ok().body(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Retrieved all collaborators by todo id : %d".formatted(todo_id),
                collaborators));
    }


    // Add a new collaborator to 'todo_list'
    @PostMapping("/{user_id}/todos/{todo_id}/collaborators")
    public ResponseEntity<?> addCollaborator(
            @PathVariable("user_id") long user_id,
            @PathVariable("todo_id") long todo_id,
            @RequestBody CollaboratorRequest request) {


        String currentUserName = auth.getCurrentUser();

        if (! currentUserName.equals(userService.readById(user_id).getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not owner the todo");
        }

        User user = userService.readById(user_id);
        ToDo todo = toDoService.readById(todo_id);

        List<Task> tasks = user.getMyTodos().stream()
                .flatMap(toDo -> toDo.getTasks().stream())
                .toList();

        if (tasks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "There are no tasks",
                    null
            ));
        }

        boolean isCollaboratorExists = todo.getCollaborators().stream()
                .anyMatch(collaborator -> collaborator.getId().equals(request.getCollaborator_id()));

        boolean isTodoOwnerTheSameAsCollaborator = todo.getOwner().getId().equals(request.getCollaborator_id());


        if (isCollaboratorExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This collaborator already assigned to this todo");
        }
        if (isTodoOwnerTheSameAsCollaborator) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This collaborator is the same as owner of this todo");
        }


        User collaborator = userService.readById(request.getCollaborator_id());
        todo.getCollaborators().add(collaborator);
        toDoService.update(todo);

        return ResponseEntity.ok().body(
                new ApiResponse<>(HttpStatus.OK.value(),
                        "Collaborator with id %d was added to todo with id %d".formatted(request.getCollaborator_id(), todo_id),
                        null));
    }

    @PutMapping("/{user_id}/update/{todo_id}")
    public ResponseEntity<?> updateTodo(@PathVariable("todo_id") long todo_id,
                                        @PathVariable("user_id") long user_id,
                                        @RequestBody TodoRequest todoRequest) {

        User user = userService.readById(user_id);

        String currentUserName = auth.getCurrentUser();

        if (! currentUserName.equals(userService.readById(user_id).getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not owner this todo");
        }
        Optional<ToDo> findingTodo = user.getMyTodos().stream()
                .filter(toDo -> toDo.getId().equals(todo_id)).findAny();
        if (findingTodo.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Todo not found");

        ToDo toDo = toDoService.readById(todo_id);

            toDo.setTitle(todoRequest.title());
            toDo.setCreatedAt(LocalDateTime.now());
            toDoService.update(toDo);

        return ResponseEntity.ok().body("Todo with id %d was successfully updated".formatted(todo_id));
    }

    @DeleteMapping("/delete/{todo_id}/users/{user_id}")
    public ResponseEntity<?> deleteTask(@PathVariable("todo_id") long todo_id,
                                        @PathVariable("user_id") long user_id) {

        String currentUserName = auth.getCurrentUser();

        if (! currentUserName.equals(userService.readById(user_id).getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not owner this todo");
        }

        toDoService.delete(todo_id);

        return ResponseEntity.ok().body("Todo with id %d was successfully deleted".formatted(todo_id));
    }


}
