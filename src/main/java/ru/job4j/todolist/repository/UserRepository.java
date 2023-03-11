package ru.job4j.todolist.repository;

import ru.job4j.todolist.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    User add(User user);

    Optional<User> findByLoginAndPassword(String login, String password);

    Optional<User> findById(int id);

    Collection<User> findAll();

    boolean delete(int id);

}
