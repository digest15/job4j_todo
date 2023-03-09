package ru.job4j.todolist.service;

import ru.job4j.todolist.model.Task;

import java.util.List;

public interface TaskService {
    Task add(Task task);

    boolean update(Task task);

    boolean delete(int id);

    List<Task> findAll();

    List<Task> findAll(boolean isDone);

    Task findById(int id);

    List<Task> findLikeDescription(String key);

    boolean doneTask(Task task, Boolean isDone);
}
