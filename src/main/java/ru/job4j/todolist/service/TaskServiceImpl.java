package ru.job4j.todolist.service;

import org.springframework.stereotype.Service;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.model.User;
import ru.job4j.todolist.repository.TaskRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private static final ZoneId SERVICE_TIME_ZONE_ID = ZoneId.of("UTC");

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task add(Task task) {
        return taskRepository.add(computeToUTC(task));
    }

    @Override
    public boolean update(Task task) {
        return taskRepository.update(computeToUTC(task));
    }

    @Override
    public boolean delete(int id) {
        return taskRepository.delete(id);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll()
                .stream()
                .map(this::computeFromUTC).collect(Collectors.toList());
    }

    @Override
    public List<Task> findAll(boolean isDone) {
        return taskRepository.findByDone(isDone)
                .stream()
                .map(this::computeFromUTC).collect(Collectors.toList());
    }

    @Override
    public Task findById(int id) {
        return taskRepository.findById(id)
                .map(this::computeFromUTC)
                .orElse(null);
    }

    @Override
    public List<Task> findLikeDescription(String key) {
        return findLikeDescription(key)
                .stream()
                .map(this::computeFromUTC).collect(Collectors.toList());
    }

    @Override
    public boolean doneTask(Task task, Boolean isDone) {
        Objects.requireNonNull(isDone);
        return taskRepository.updateByDone(task.getId(), isDone);
    }

    private Task computeFromUTC(Task task) {
        ZoneId userZoneId = userTimeZoneOrDefault.apply(task.getUser());
        LocalDateTime computedDateTime = task.getCreated()
                .atZone(SERVICE_TIME_ZONE_ID)
                .withZoneSameInstant(userZoneId).toLocalDateTime();
        task.setCreated(computedDateTime);
        return task;
    }

    private Task computeToUTC(Task task) {
        ZoneId userZoneId = userTimeZoneOrDefault.apply(task.getUser());
        LocalDateTime computedDateTime = task.getCreated()
                .atZone(userZoneId)
                .withZoneSameInstant(SERVICE_TIME_ZONE_ID).toLocalDateTime();
        task.setCreated(computedDateTime);
        return task;
    }

    private final Function<User, ZoneId> userTimeZoneOrDefault = (user) -> Optional.ofNullable(user)
            .map(User::getTimeZoneId)
            .map(ZoneId::of)
            .orElseGet(() -> TimeZone.getDefault().toZoneId());

}
