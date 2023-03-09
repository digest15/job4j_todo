package ru.job4j.todolist.service;

import org.springframework.stereotype.Service;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.repository.TaskRepository;

import java.util.List;
import java.util.Objects;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task add(Task task) {
        return taskRepository.add(task);
    }

    @Override
    public boolean update(Task task) {
        return taskRepository.update(task);
    }

    @Override
    public boolean delete(int id) {
        return taskRepository.delete(id);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> findAll(boolean isDone) {
        return taskRepository.findByDone(isDone);
    }

    @Override
    public Task findById(int id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public List<Task> findLikeDescription(String key) {
        return findLikeDescription(key);
    }

    @Override
    public boolean doneTask(Task task, Boolean isDone) {
        Objects.requireNonNull(isDone);
        return taskRepository.updateByDone(task.getId(), isDone);
    }
}
