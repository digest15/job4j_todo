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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CategoryRepositoryImplTest {

    private static CategoryRepository categoryRepository;

    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void initRepository() throws Exception {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        CrudRepository crudRepository = new CrudRepository(sessionFactory);
        categoryRepository = new CategoryRepositoryImpl(crudRepository);

        categoryRepository.findAll()
                .forEach(priority -> categoryRepository.delete(priority));
    }

    @AfterAll
    public static void clear() {
        sessionFactory.close();
    }

    @AfterEach
    public void clearRepository() {
        categoryRepository.findAll()
                .forEach(priority -> categoryRepository.delete(priority));
    }

    @Test
    public void whenSaveThenGetSame() {
        var category1 = categoryRepository.add(new Category(0, "Category1"));
        assertThat(category1).isNotNull();

        var expected = categoryRepository.findById(category1.getId());
        assertThat(expected).isPresent();
        assertThat(expected.get()).isEqualTo(category1);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var category1 = categoryRepository.add(new Category(0, "Category1"));
        var category2 = categoryRepository.add(new Category(0, "Category2"));
        var category3 = categoryRepository.add(new Category(0, "Category3"));

        List<Category> list = categoryRepository.findAll();
        assertThat(list).isEqualTo(List.of(category1, category2, category3));
    }

    @Test
    public void whenFindById() {
        var category1 = categoryRepository.add(new Category(0, "Category1"));

        var expected = categoryRepository.findById(category1.getId());
        assertThat(expected).isPresent();
        assertThat(expected.get()).isEqualTo(category1);

        expected = categoryRepository.findById(0);
        assertThat(expected).isEmpty();
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(categoryRepository.findAll()).isEqualTo(Collections.emptyList());
        assertThat(categoryRepository.findById(0)).isEqualTo(Optional.empty());
    }

    @Test
    public void whenDelete() {
        var category1 = categoryRepository.add(new Category(0, "Category1"));

        var isDel = categoryRepository.delete(category1);
        assertThat(isDel).isTrue();

        var expected = categoryRepository.findById(category1.getId());
        assertThat(expected).isEmpty();

        isDel = categoryRepository.delete(category1);
        assertThat(isDel).isFalse();
    }
}