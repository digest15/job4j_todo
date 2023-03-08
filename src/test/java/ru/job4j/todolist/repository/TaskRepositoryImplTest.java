package ru.job4j.todolist.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.todolist.model.Task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TaskRepositoryImplTest {

    private static TaskRepository taskRepository;

    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void initRepository() throws Exception {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        taskRepository = new TaskRepositoryImpl(sessionFactory);
    }

    @AfterAll
    public static void clear() {
        sessionFactory.close();
    }

    @AfterEach
    public void clearRepository() {
        taskRepository.findAll()
                .forEach(task -> taskRepository.delete(task));
    }

    @Test
    public void whenSaveThenGetSame() {
        var task = Task.builder()
                .done(false)
                .created(LocalDateTime.now())
                .description("task")
                .build();
        taskRepository.add(task);
        Optional<Task> findTask = taskRepository.findById(task.getId());

        assertThat(findTask.isPresent()).isTrue();
        assertThat(task).isEqualTo(findTask.get());
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var task1 = taskRepository.add(Task.builder().created(creationDate).description("task1").build());
        var task2 = taskRepository.add(Task.builder().created(creationDate).description("task2").build());
        var task3 = taskRepository.add(Task.builder().created(creationDate).description("task3").build());
        var result = taskRepository.findAll();

        assertThat(result).isEqualTo(List.of(task1, task2, task3));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(taskRepository.findAll()).isEqualTo(emptyList());
        assertThat(taskRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var task = taskRepository.add(Task.builder().created(creationDate).description("Task").build());
        taskRepository.delete(task);
        Optional<Task> foundTask = taskRepository.findById(task.getId());
        assertThat(foundTask).isEqualTo(Optional.empty());
    }

    @Test
    public void whenUpdateThenGetUpdated() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var task = taskRepository.add(Task.builder().created(creationDate).description("Task").build());
        var updateTask = Task.builder()
                .id(task.getId())
                .created(creationDate.plusDays(1))
                .description("new Task")
                .done(true)
                .build();
        taskRepository.update(updateTask);
        var savedTask = taskRepository.findById(task.getId());

        assertThat(savedTask).isPresent();
        assertThat(savedTask.get()).isEqualTo(updateTask);
    }
}