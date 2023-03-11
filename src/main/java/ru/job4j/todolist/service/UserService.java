package ru.job4j.todolist.service;

import ru.job4j.todolist.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {

    User add(User user);

    Optional<User> findByLoginAndPassword(String login, String password);

    boolean delete(int id);

    Collection<User> findAll();

}
