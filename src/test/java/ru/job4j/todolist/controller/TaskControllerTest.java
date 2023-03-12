package ru.job4j.todolist.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.model.User;
import ru.job4j.todolist.service.TaskService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    private TaskService taskService;

    private TaskController taskController;

    @BeforeEach
    public void initService() {
        taskService = mock(TaskService.class);
        taskController = new TaskController(taskService);
    }

    @Test
    public void whenRequestAllTaskList() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null);
        var task2 = new Task(1, "Task2", LocalDateTime.now(), false, null);
        var expectedTasks = List.of(task1, task2);
        when(taskService.findAll()).thenReturn(expectedTasks);

        var model = new ConcurrentModel();
        var view = taskController.getAll(model);
        var actualTasks = model.get("tasks");

        assertThat(view).isEqualTo("tasks/list");
        assertThat(actualTasks).isEqualTo(expectedTasks);
    }

    @Test
    public void whenRequestDoneTaskList() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), true, null);
        var task2 = new Task(1, "Task2", LocalDateTime.now(), true, null);
        var expectedTasks = List.of(task1, task2);
        when(taskService.findAll(true)).thenReturn(expectedTasks);

        var model = new ConcurrentModel();
        var view = taskController.getAllDone(model);
        var actualTasks = model.get("tasks");

        assertThat(view).isEqualTo("tasks/listDone");
        assertThat(actualTasks).isEqualTo(expectedTasks);
    }

    @Test
    public void whenRequestNewTaskList() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null);
        var task2 = new Task(1, "Task2", LocalDateTime.now(), false, null);
        var expectedTasks = List.of(task1, task2);
        when(taskService.findAll(false)).thenReturn(expectedTasks);

        var model = new ConcurrentModel();
        var view = taskController.getAllNew(model);
        var actualTasks = model.get("tasks");

        assertThat(view).isEqualTo("tasks/listNew");
        assertThat(actualTasks).isEqualTo(expectedTasks);
    }

    @Test
    public void whenGetCreationPage() {
        var view = taskController.getCreationPage();
        assertThat(view).isEqualTo("tasks/create");
    }

    @Test
    public void whenPostTask() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null);
        var taskArgCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskService.add(taskArgCaptor.capture())).thenReturn(task1);

        var model = new ConcurrentModel();
        var view = taskController.create(task1, model);
        var actualTask = taskArgCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/tasks");
        assertThat(actualTask).isEqualTo(task1);
    }

    @Test
    public void whenPostTaskAndNotSaveThenRedirectErrorPage() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null);
        when(taskService.add(any(Task.class))).thenReturn(null);

        var model = new ConcurrentModel();
        var view = taskController.create(task1, model);
        var errorMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(errorMessage).isEqualTo("Error when save Task " + task1);
    }

    @Test
    public void whenDoneTask() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null);
        var taskArgCaptor = ArgumentCaptor.forClass(Task.class);
        var doneFlagCaptor = ArgumentCaptor.forClass(Boolean.class);
        when(taskService.doneTask(taskArgCaptor.capture(), doneFlagCaptor.capture())).thenReturn(true);

        var isDone = true;
        var model = new ConcurrentModel();
        var view = taskController.setDone(task1, isDone, model);
        var captorTask = taskArgCaptor.getValue();
        var captorFlag = doneFlagCaptor.getValue();
        var actualTask = model.getAttribute("task");

        assertThat(view).isEqualTo("redirect:/tasks/" + task1.getId());
        assertThat(actualTask).isEqualTo(task1);
        assertThat(captorTask).isEqualTo(task1);
        assertThat(captorFlag).isEqualTo(isDone);
    }

    @Test
    public void whenDoneTaskAndNotUpdated() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null);
        var taskArgCaptor = ArgumentCaptor.forClass(Task.class);
        var doneFlagCaptor = ArgumentCaptor.forClass(Boolean.class);
        when(taskService.doneTask(taskArgCaptor.capture(), doneFlagCaptor.capture())).thenReturn(false);

        var isDone = false;
        var model = new ConcurrentModel();
        var view = taskController.setDone(task1, isDone, model);
        var captorTask = taskArgCaptor.getValue();
        var captorFlag = doneFlagCaptor.getValue();
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Error when update task " + task1);
        assertThat(captorTask).isEqualTo(task1);
        assertThat(captorFlag).isEqualTo(isDone);
    }

    @Test
    public void whenUpdateTask() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null);
        var taskArgCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskService.update(taskArgCaptor.capture())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = taskController.update(task1, model);
        var captorTask = taskArgCaptor.getValue();
        var actualTask = model.getAttribute("task");

        assertThat(view).isEqualTo("redirect:/tasks/" + task1.getId());
        assertThat(actualTask).isEqualTo(task1);
        assertThat(captorTask).isEqualTo(task1);
    }

    @Test
    public void whenUpdateTaskAndNotUpdated() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null);
        var taskArgCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskService.update(taskArgCaptor.capture())).thenReturn(false);

        var model = new ConcurrentModel();
        var view = taskController.update(task1, model);
        var captorTask = taskArgCaptor.getValue();
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo("Error when update task " + task1);
        assertThat(captorTask).isEqualTo(task1);
    }

    @Test
    public void whenRequestTaskById() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null);
        when(taskService.findById(any(Integer.class))).thenReturn(task1);

        var model = new ConcurrentModel();
        var view = taskController.getById(model, task1.getId());
        var actualTask = model.getAttribute("task");

        assertThat(view).isEqualTo("tasks/one");
        assertThat(actualTask).isEqualTo(task1);
    }

    @Test
    public void whenRequestTaskByIdAndNotFoundThenRedirectErrorPage() {
        when(taskService.findById(any(Integer.class))).thenReturn(null);
        var id = 0;

        var model = new ConcurrentModel();
        var view = taskController.getById(model, id);
        var errorMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(errorMessage).isEqualTo("Not found task by ID: " + id);
    }

    @Test
    public void whenRequestEditPage() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null);
        when(taskService.findById(any(Integer.class))).thenReturn(task1);

        var model = new ConcurrentModel();
        var view = taskController.edit(model, task1.getId());
        var actualTask = model.getAttribute("task");

        assertThat(view).isEqualTo("tasks/edit");
        assertThat(actualTask).isEqualTo(task1);
    }

    @Test
    public void whenRequestEditPageEndTaskNotFound() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null);
        when(taskService.findById(any(Integer.class))).thenReturn(null);

        var model = new ConcurrentModel();
        var view = taskController.edit(model, task1.getId());
        var errorMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(errorMessage).isEqualTo("Not found task by ID: " + task1.getId());
    }

    @Test
    public void whenDeleteTask() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null);
        var idCaptor = ArgumentCaptor.forClass(Integer.class);
        when(taskService.delete(idCaptor.capture())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = taskController.delete(model, task1.getId());
        var captorId = idCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/tasks");
        assertThat(captorId).isEqualTo(task1.getId());
    }

    @Test
    public void whenDeleteTaskAndNotDeleted() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null);
        var idCaptor = ArgumentCaptor.forClass(Integer.class);
        when(taskService.delete(idCaptor.capture())).thenReturn(false);

        var model = new ConcurrentModel();
        var view = taskController.delete(model, task1.getId());
        var captorId = idCaptor.getValue();
        var errorMessage = model.getAttribute("message");

        assertThat(captorId).isEqualTo(task1.getId());
        assertThat(view).isEqualTo("errors/404");
        assertThat(errorMessage).isEqualTo("Error when delete Task for id: " + task1.getId());
    }
}