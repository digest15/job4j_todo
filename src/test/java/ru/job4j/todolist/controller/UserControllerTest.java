package ru.job4j.todolist.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.todolist.model.User;
import ru.job4j.todolist.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserController userController;

    private UserService userService;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void whenGetRegistrationPage() {
        var view = userController.getRegistrationPage();
        assertThat(view).isEqualTo("users/create");
    }

    @Test
    void whenRegisterNewUserThenRedirectToHomePage() {
        var user = new User(0, "name", "login", "password");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.add(userArgumentCaptor.capture())).thenReturn(user);

        var model = new ConcurrentModel();
        var view = userController.register(model, user);

        assertThat(view).isEqualTo("index");
        assertThat(userArgumentCaptor.getValue()).isEqualTo(user);
    }

    @Test
    void whenRegisterOldUserThenRedirectToErrorPage() {
        var user = new User(0, "name", "login", "password");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.add(userArgumentCaptor.capture())).thenReturn(null);

        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var message = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(message).isEqualTo("Пользователь с таким логином уже существует");
        assertThat(userArgumentCaptor.getValue()).isEqualTo(user);
    }

    @Test
    void whenGetLoginUserPage() {
        var view = userController.getLoginPage();
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    void whenPostLoginUserThenRedirectToTasksAndUserInSession() {
        var user = new User(0, "name", "login", "password");
        when(userService.findByLoginAndPassword(any(), any())).thenReturn(Optional.of(user));
        MockHttpServletRequest request = new MockHttpServletRequest();

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, request);
        var actualUser = (User) request.getSession().getAttribute("user");

        assertThat(view).isEqualTo("redirect:/tasks");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    void whenPostLoginWithWrongUserThenRedirectToErrorPage() {
        var user = new User(0, "name", "login", "password");
        when(userService.findByLoginAndPassword(any(), any())).thenReturn(Optional.empty());
        MockHttpServletRequest request = new MockHttpServletRequest();

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, request);
        var message = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(message).isEqualTo("Логин или пароль введены неверно");
    }

    @Test
    void whenLoginAndLogoutThenRedirectLoginPageAndEmptySession() {
        var user = new User(0, "name", "login", "password");
        when(userService.findByLoginAndPassword(any(), any())).thenReturn(Optional.of(user));
        MockHttpServletRequest request = new MockHttpServletRequest();

        var model = new ConcurrentModel();
        var afterLoginView = userController.loginUser(user, model, request);
        var afterLogoutView = userController.logout(request.getSession());

        assertThat(afterLogoutView).isEqualTo("redirect:/users/login");
        assertThat(request.getSession().getAttribute("user")).isNull();
    }
}