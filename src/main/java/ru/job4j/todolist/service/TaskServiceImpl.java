package ru.job4j.todolist.service;

import org.springframework.stereotype.Service;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.repository.TaskRepository;

import java.util.List;
import java.util.Objects;
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
    public Task findById(int id) {
        return taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(
                String.format("Wrong id %s", id)
        ));
    }

    @Override
    public List<Task> findLikeDescription(String key) {
        return findLikeDescription(key);
    }

    @Override
    public void doneTask(Task task, Boolean isDone) {
        Objects.requireNonNull(isDone);
        task.setDone(isDone);
        taskRepository.update(task);
    }
}
