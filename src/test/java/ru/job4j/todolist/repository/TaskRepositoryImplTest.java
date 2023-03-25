package ru.job4j.todolist.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.todolist.model.Category;
import ru.job4j.todolist.model.Priority;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.model.User;

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

    private static UserRepository userRepository;

    private static PriorityRepository priorityRepository;

    private static CategoryRepository categoryRepository;

    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void initRepository() throws Exception {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        CrudRepository crudRepository = new CrudRepository(sessionFactory);
        taskRepository = new TaskRepositoryImpl(crudRepository);
        userRepository = new UserRepositoryImpl(crudRepository);
        priorityRepository = new PriorityRepositoryImpl(crudRepository);
        categoryRepository = new CategoryRepositoryImpl(crudRepository);

        priorityRepository.findAll()
                .forEach(priority -> priorityRepository.delete(priority));
        categoryRepository.findAll()
                .forEach(category -> categoryRepository.delete(category));
    }

    @AfterAll
    public static void clear() {
        sessionFactory.close();
    }

    @AfterEach
    public void clearRepository() {
        taskRepository.findAll()
                .forEach(task -> taskRepository.delete(task.getId()));
        userRepository.findAll()
                .forEach(user -> userRepository.delete(user));
        priorityRepository.findAll()
                .forEach(priority -> priorityRepository.delete(priority));
        categoryRepository.findAll()
                .forEach(category -> categoryRepository.delete(category));
    }

    @Test
    public void whenSaveThenGetSame() {
        var user = userRepository.add(new User(0, "admin", "admin", "123"));
        var priority = priorityRepository.add(new Priority(0, "high", 1));
        var category1 = categoryRepository.add(new Category(0, "Category1"));
        var category2 = categoryRepository.add(new Category(0, "Category2"));
        var task = taskRepository.add(Task.builder()
                .done(false)
                .created(LocalDateTime.now())
                .description("task")
                .user(user)
                .priority(priority)
                .categories(List.of(category1, category2))
                .build());
        Optional<Task> findTask = taskRepository.findById(task.getId());

        assertThat(findTask.isPresent()).isTrue();
        assertThat(task).isEqualTo(findTask.get());
        assertThat(findTask.get().getUser()).isEqualTo(user);
        assertThat(findTask.get().getPriority()).isEqualTo(priority);
        assertThat(findTask.get().getCategories()).asList().contains(category1, category2);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var user = userRepository.add(new User(0, "admin", "admin", "123"));
        var priority = priorityRepository.add(new Priority(0, "high", 1));
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);

        var category1 = categoryRepository.add(new Category(0, "Category1"));
        var category2 = categoryRepository.add(new Category(0, "Category2"));
        List<Category> category11 = List.of(category1, category2);

        var task1 = taskRepository.add(Task.builder()
                .created(creationDate)
                .description("task1")
                .user(user)
                .priority(priority)
                .categories(category11)
                .build()
        );
        var task2 = taskRepository.add(Task.builder()
                .created(creationDate)
                .description("task2")
                .user(user)
                .priority(priority)
                .categories(category11)
                .build()
        );
        var task3 = taskRepository.add(Task.builder()
                .created(creationDate)
                .description("task3")
                .user(user)
                .priority(priority)
                .categories(category11)
                .build()
        );
        var result = taskRepository.findAll();

        assertThat(task1).isNotNull();
        assertThat(task2).isNotNull();
        assertThat(task3).isNotNull();
        assertThat(result).isEqualTo(List.of(task1, task2, task3));
        assertThat(result.get(0).getPriority()).isEqualTo(priority);
        assertThat(result.get(0).getCategories()).asList().contains(category1, category2);
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(taskRepository.findAll()).isEqualTo(emptyList());
        assertThat(taskRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        var user = userRepository.add(new User(0, "admin", "admin", "123"));
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var priority = priorityRepository.add(new Priority(0, "high", 1));
        var task = taskRepository.add(Task.builder()
                .created(creationDate)
                .description("Task")
                .user(user)
                .priority(priority)
                .build()
        );
        assertThat(task).isNotNull();

        taskRepository.delete(task.getId());
        Optional<Task> foundTask = taskRepository.findById(task.getId());
        assertThat(foundTask).isEqualTo(Optional.empty());

        var foundUser = userRepository.findById(user.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(user);

        var foundPriority = priorityRepository.findById(priority.getId());
        assertThat(foundPriority).isPresent();
        assertThat(foundPriority.get()).isEqualTo(priority);
    }

    @Test
    public void whenUpdateThenGetUpdated() {
        var user1 = userRepository.add(new User(0, "admin", "admin", "123"));
        var user2 = userRepository.add(new User(0, "admin1", "admin1", "123"));
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var priority = priorityRepository.add(new Priority(0, "high", 1));
        var task = taskRepository.add(Task.builder()
                .created(creationDate)
                .description("Task")
                .user(user1)
                .priority(priority)
                .build()
        );
        var updateTask = Task.builder()
                .id(task.getId())
                .created(creationDate.plusDays(1))
                .description("new Task")
                .done(true)
                .user(user2)
                .priority(priority)
                .build();
        taskRepository.update(updateTask);
        var savedTask = taskRepository.findById(task.getId());

        assertThat(savedTask).isPresent();
        assertThat(savedTask.get()).isEqualTo(updateTask);
        assertThat(savedTask.get().getUser()).isEqualTo(user2);
        assertThat(savedTask.get().getPriority()).isEqualTo(priority);
    }

    @Test
    public void whenUpdateByDone() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var priority = priorityRepository.add(new Priority(0, "high", 1));
        var task = taskRepository.add(Task.builder()
                .created(creationDate)
                .description("Task")
                .done(false)
                .priority(priority)
                .build()
        );

        var isDone = true;
        taskRepository.updateByDone(task.getId(), isDone);
        var actualTask = taskRepository.findById(task.getId());
        assertThat(actualTask).isPresent();
        assertThat(actualTask.get().isDone()).isEqualTo(isDone);
    }

    @Test
    public void whenFindTaskByDone() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var priority = priorityRepository.add(new Priority(0, "high", 1));
        var task1 = taskRepository.add(Task.builder()
                .created(creationDate)
                .description("Task1")
                .done(true)
                .priority(priority)
                .build()
        );
        var task2 = taskRepository.add(Task.builder()
                .created(creationDate)
                .description("Task2")
                .done(false)
                .priority(priority)
                .build()
        );

        List<Task> newTask = taskRepository.findByDone(true);
        assertThat(newTask).isEqualTo(List.of(task1));
        assertThat(newTask.get(0).getPriority()).isEqualTo(priority);

        List<Task> doneTask = taskRepository.findByDone(false);
        assertThat(doneTask).isEqualTo(List.of(task2));
        assertThat(doneTask.get(0).getPriority()).isEqualTo(priority);
    }
}