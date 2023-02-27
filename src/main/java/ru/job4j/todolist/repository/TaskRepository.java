package ru.job4j.todolist.repository;

import ru.job4j.todolist.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task add(Task task);

    void update(Task task);

    void delete(Task task);

    List<Task> findAll();

    Optional<Task> findById(int id);

    List<Task> findLikeDescription(String key);
}
