package com.softserve.itacademy.todolist.repository;

import com.softserve.itacademy.todolist.model.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    @Query(value = "select t.id, t.title, t.created_at, t.owner_id\n" +
                   "from todos t\n" +
                   "where t.owner_id = 1\n" +
                   "union\n" +
                   "select t.id, t.title,t.created_at, t.owner_id\n" +
                   "from todos t\n" +
                   "         inner join todo_collaborator tc\n" +
                   "                    on t.id = tc.todo_id and tc.collaborator_id = 1;",
            nativeQuery = true)
    List<ToDo> getByUserId(@Param("userId") long userId);
}
