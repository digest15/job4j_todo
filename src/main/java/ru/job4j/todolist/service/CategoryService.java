package ru.job4j.todolist.service;

import ru.job4j.todolist.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Category add(Category category);

    boolean update(Category category);

    boolean delete(Category category);

    List<Category> findAll();

    Optional<Category> findById(int id);

}
