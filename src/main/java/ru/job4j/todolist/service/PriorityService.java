package ru.job4j.todolist.service;

import ru.job4j.todolist.model.Priority;

import java.util.List;
import java.util.Optional;

public interface PriorityService {

    Priority add(Priority priority);

    boolean update(Priority priority);

    boolean delete(Priority priority);

    Optional<Priority> findById(int id);

    List<Priority> findAll();

}
