package ru.job4j.todolist.repository;

import ru.job4j.todolist.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task add(Task task);

    boolean update(Task task);

    boolean updateByDone(int id, boolean isDone);

    boolean delete(int id);

    List<Task> findAll();

    Optional<Task> findById(int id);

    List<Task> findLikeDescription(String key);

    List<Task> findByDone(boolean isDone);

}
