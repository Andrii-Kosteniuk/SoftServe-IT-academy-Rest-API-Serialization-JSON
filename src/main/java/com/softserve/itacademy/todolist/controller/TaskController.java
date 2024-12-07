package com.softserve.itacademy.todolist.controller;

import com.softserve.itacademy.todolist.config.Auth;
import com.softserve.itacademy.todolist.dto.ApiResponse;
import com.softserve.itacademy.todolist.dto.taskDto.TaskRequest;
import com.softserve.itacademy.todolist.dto.taskDto.TaskResponse;
import com.softserve.itacademy.todolist.dto.taskDto.TaskTransformer;
import com.softserve.itacademy.todolist.model.State;
import com.softserve.itacademy.todolist.model.Task;
import com.softserve.itacademy.todolist.model.ToDo;
import com.softserve.itacademy.todolist.model.User;
import com.softserve.itacademy.todolist.repository.TaskRepository;
import com.softserve.itacademy.todolist.service.StateService;
import com.softserve.itacademy.todolist.service.TaskService;
import com.softserve.itacademy.todolist.service.ToDoService;
import com.softserve.itacademy.todolist.service.UserService;
import com.softserve.itacademy.todolist.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final UserService userService;
    private final TaskRepository taskRepository;
    private final ToDoService toDoService;
    private final StateService stateService;
    private final TaskService taskService;
    private final Auth auth;
    private final UserServiceImpl userServiceImpl;


    // Get all tasks by given user and todo_id
    @GetMapping("{user_id}/todos/{todo_id}/tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAllTasksByUserId(
            @PathVariable("user_id") long user_id,
            @PathVariable("todo_id") long todo_id) {
        User user = userService.readById(user_id);

        List<ToDo> listOfTodo = user.getMyTodos().stream().toList();

        List<ToDo> toDos = listOfTodo.stream().filter(toDo -> toDo.getId().equals(todo_id)).toList();

        if (toDos.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(),
                    "There any todo found by given todo Id",
                    null
            ));
        }

        List<TaskResponse> tasks = toDos.stream().flatMap(toDo -> toDo.getTasks().stream()
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

    // Create task related to todo_id
    @PostMapping("/{todo_id}/tasks")
    public ResponseEntity<?> createTask(@PathVariable("todo_id") long todo_id, @RequestBody TaskRequest request) {
        List<Task> tasks = taskRepository.getByTodoId(todo_id);
        if (tasks.stream().map(Task::getName).anyMatch(request.name()::equals)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task already exists");
        }
        ToDo toDo = toDoService.readById(todo_id);
        State state;
        try {
            state = stateService.getByName("OPEN");
        } catch (EntityNotFoundException e) {
            state = stateService.create(new State("OPEN"));
        }

        Task task = TaskTransformer.fillTaskDataFromRequest(request, toDo, state);
        taskService.create(task);
        TaskResponse taskResponse = new TaskResponse(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.CREATED.value(), "New task was created for todo with id %d".formatted(todo_id), taskResponse));
    }

    // Update task by user_id by task_id
    @PutMapping("/{user_id}/update/{task_id}")
    public ResponseEntity<?> updateTask(@PathVariable("task_id") long task_id,
                                        @PathVariable("user_id") long user_id,
                                        @RequestBody TaskRequest taskRequest) {

        User user = userService.readById(user_id);

        List<Task> tasks = user.getMyTodos().stream()
                .flatMap(toDo -> toDo.getTasks()
                        .stream()).toList();

        tasks.stream().filter(t -> t.getId().equals(task_id))
                .findFirst().ifPresent(t -> {
                    Task task = TaskTransformer.fillTaskDataFromRequest(taskRequest, t.getTodo(), t.getState());
                    task.setId(task_id);
                    taskService.update(task);
                });

        return ResponseEntity.ok().body("Task with id %d was successfully updated".formatted(task_id));
    }

    // Delete task by user_id by task_id
    @DeleteMapping("/delete/{task_id}/users/{user_id}")
    public ResponseEntity<?> deleteTask(@PathVariable("task_id") long task_id,
                                        @PathVariable("user_id") long user_id) {

        String currentUserName = auth.getCurrentUser();

        if (!currentUserName.equals(userServiceImpl.readById(user_id).getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not owner the task");
        }

        taskService.delete(task_id);

        return ResponseEntity.ok().body("Task with id %d was successfully deleted".formatted(task_id));
    }


}
