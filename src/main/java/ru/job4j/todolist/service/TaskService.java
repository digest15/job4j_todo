package ru.job4j.todolist.service;

import ru.job4j.todolist.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    Task add(Task task);

    void update(Task task);

    void delete(Task task);

    List<Task> findAll();

    List<Task> findAll(boolean isDone);

    Optional<Task> findById(int id);

    List<Task> findLikeDescription(String key);
}
