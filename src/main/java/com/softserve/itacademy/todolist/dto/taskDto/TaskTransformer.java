package com.softserve.itacademy.todolist.dto.taskDto;

import com.softserve.itacademy.todolist.model.Priority;
import com.softserve.itacademy.todolist.model.State;
import com.softserve.itacademy.todolist.model.Task;
import com.softserve.itacademy.todolist.model.ToDo;

public class TaskTransformer {
    public static TaskDto convertToDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getName(),
                task.getPriority().toString(),
                task.getTodo().getId(),
                task.getState().getId()
        );
    }

    public static Task convertToEntity(TaskDto taskDto, ToDo todo, State state) {
        Task task = new Task();
        task.setId(taskDto.getId());
        task.setName(taskDto.getName());
        task.setPriority(Priority.valueOf(taskDto.getPriority()));
        task.setTodo(todo);
        task.setState(state);
        return task;
    }

    public static Task fillTaskDataFromRequest(TaskRequest request, ToDo todo, State state) {
        Task task = new Task();
        task.setName(request.name());
        task.setPriority(Priority.valueOf(request.priority()));
        task.setTodo(todo);
        task.setState(state);
        return task;
    }
}
