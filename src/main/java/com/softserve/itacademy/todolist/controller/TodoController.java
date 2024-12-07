package com.softserve.itacademy.todolist.controller;

import com.softserve.itacademy.todolist.dto.ApiResponse;
import com.softserve.itacademy.todolist.dto.taskDto.TaskRequest;
import com.softserve.itacademy.todolist.dto.taskDto.TaskResponse;
import com.softserve.itacademy.todolist.dto.taskDto.TaskTransformer;
import com.softserve.itacademy.todolist.model.State;
import com.softserve.itacademy.todolist.model.Task;
import com.softserve.itacademy.todolist.model.ToDo;
import com.softserve.itacademy.todolist.repository.TaskRepository;
import com.softserve.itacademy.todolist.service.StateService;
import com.softserve.itacademy.todolist.service.TaskService;
import com.softserve.itacademy.todolist.service.ToDoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TaskService taskService;
    private final ToDoService toDoService;
    private final StateService stateService;
    private final TaskRepository taskRepository;

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
        }catch (EntityNotFoundException e) {
            state = stateService.create(new State("OPEN"));
        }

        Task task = TaskTransformer.fillTaskDataFromRequest(request, toDo, state);
        taskService.create(task);
        TaskResponse taskResponse = new TaskResponse(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.CREATED.value(), "New task was created for todo with id %d".formatted(todo_id), taskResponse));
    }

}
