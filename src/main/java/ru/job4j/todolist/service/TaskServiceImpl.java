package ru.job4j.todolist.service;

import org.springframework.stereotype.Service;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.repository.TaskRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public void update(Task task) {
        taskRepository.update(task);
    }

    @Override
    public void delete(Task task) {
        taskRepository.delete(task);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> findAll(boolean isDone) {
        return taskRepository.findAll()
                .stream()
                .filter(task -> task.isDone() == isDone)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Task> findById(int id) {
        return taskRepository.findById(id);
    }

    @Override
    public List<Task> findLikeDescription(String key) {
        return findLikeDescription(key);
    }
}
