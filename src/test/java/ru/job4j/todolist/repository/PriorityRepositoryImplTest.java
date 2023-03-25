package ru.job4j.todolist.repository;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.todolist.model.Priority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PriorityRepositoryImplTest {
    private static PriorityRepository priorityRepository;

    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void initRepository() throws Exception {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        CrudRepository crudRepository = new CrudRepository(sessionFactory);
        priorityRepository = new PriorityRepositoryImpl(crudRepository);

        priorityRepository.findAll()
                .forEach(priority -> priorityRepository.delete(priority));
    }

    @AfterAll
    public static void clear() {
        sessionFactory.close();
    }

    @AfterEach
    public void clearRepository() {
        priorityRepository.findAll()
                .forEach(priority -> priorityRepository.delete(priority));
    }

    @Test
    public void whenSaveThenGetSame() {
        var priority1 = priorityRepository.add(new Priority(0, "high", 1));
        assertThat(priority1).isNotNull();

        var expected = priorityRepository.findById(priority1.getId());
        assertThat(expected).isPresent();
        assertThat(expected.get()).isEqualTo(priority1);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var priority1 = priorityRepository.add(new Priority(0, "high", 1));
        var priority2 = priorityRepository.add(new Priority(0, "low", 2));
        var priority3 = priorityRepository.add(new Priority(0, "lower", 3));

        List<Priority> list = priorityRepository.findAll();
        assertThat(list).isEqualTo(List.of(priority1, priority2, priority3));
    }

    @Test
    public void whenFindById() {
        var priority1 = priorityRepository.add(new Priority(0, "high", 1));

        var expected = priorityRepository.findById(priority1.getId());
        assertThat(expected).isPresent();
        assertThat(expected.get()).isEqualTo(priority1);

        expected = priorityRepository.findById(0);
        assertThat(expected).isEmpty();
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(priorityRepository.findAll()).isEqualTo(Collections.emptyList());
        assertThat(priorityRepository.findById(0)).isEqualTo(Optional.empty());
    }

    @Test
    public void whenDelete() {
        var priority1 = priorityRepository.add(new Priority(0, "high", 1));

        var isDel = priorityRepository.delete(priority1);
        assertThat(isDel).isTrue();

        var expected = priorityRepository.findById(priority1.getId());
        assertThat(expected).isEmpty();

        isDel = priorityRepository.delete(priority1);
        assertThat(isDel).isFalse();
    }
}