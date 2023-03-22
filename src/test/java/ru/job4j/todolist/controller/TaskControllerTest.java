package ru.job4j.todolist.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.todolist.model.Priority;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.service.PriorityService;
import ru.job4j.todolist.service.TaskService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    private TaskService taskService;

    private PriorityService priorityService;

    private TaskController taskController;

    @BeforeEach
    public void initService() {
        taskService = mock(TaskService.class);
        priorityService = mock(PriorityService.class);
        taskController = new TaskController(taskService, priorityService);
    }

    @Test
    public void whenRequestAllTaskList() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null, null);
        var task2 = new Task(1, "Task2", LocalDateTime.now(), false, null, null);
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
        var task1 = new Task(0, "Task1", LocalDateTime.now(), true, null, null);
        var task2 = new Task(1, "Task2", LocalDateTime.now(), true, null, null);
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
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null, null);
        var task2 = new Task(1, "Task2", LocalDateTime.now(), false, null, null);
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
        var high = new Priority(0, "High", 1);
        var low = new Priority(0, "low", 2);
        when(priorityService.findAll()).thenReturn(List.of(high, low));

        var model = new ConcurrentModel();
        var view = taskController.getCreationPage(model);
        var priorities = model.getAttribute("priorities");

        assertThat(view).isEqualTo("tasks/create");
        assertThat(priorities).isEqualTo(List.of(high, low));
    }

    @Test
    public void whenPostTask() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null, null);
        var taskArgCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskService.add(taskArgCaptor.capture())).thenReturn(task1);

        var priority = new Priority(0, "high", 1);
        when(priorityService.findById(any(Integer.class))).thenReturn(Optional.of(priority));

        var model = new ConcurrentModel();
        var httpSession = new MockHttpSession();
        var view = taskController.create(task1, 0, model, httpSession);
        var actualTask = taskArgCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/tasks");
        assertThat(actualTask).isEqualTo(task1);
        assertThat(actualTask.getPriority()).isEqualTo(priority);
    }

    @Test
    public void whenPostTaskAndNotSaveThenRedirectErrorPage() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null, null);
        when(taskService.add(any(Task.class))).thenReturn(null);

        var priority = new Priority(0, "high", 1);
        when(priorityService.findById(any(Integer.class))).thenReturn(Optional.of(priority));

        var model = new ConcurrentModel();
        var httpSession = new MockHttpSession();
        var view = taskController.create(task1, priority.getId(), model, httpSession);
        var errorMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(errorMessage).isEqualTo("Error when save Task " + task1);
    }

    @Test
    public void whenPostTaskAndNotFoundPriority() {
        var priorityId = 0;
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null, null);
        when(taskService.add(any(Task.class))).thenReturn(null);
        when(priorityService.findById(any(Integer.class))).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var httpSession = new MockHttpSession();
        var view = taskController.create(task1, priorityId, model, httpSession);
        var errorMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(errorMessage).isEqualTo("Not found priority by id: " + priorityId);
    }

    @Test
    public void whenDoneTask() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null, null);
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
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null, null);
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
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null, null);
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
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null, null);
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
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null, null);
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
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null, null);
        when(taskService.findById(any(Integer.class))).thenReturn(task1);

        var priority = new Priority(0, "high", 1);
        when(priorityService.findAll()).thenReturn(List.of(priority));

        var model = new ConcurrentModel();
        var view = taskController.edit(model, task1.getId());
        var actualTask = model.getAttribute("task");
        var priorities = model.getAttribute("priorities");

        assertThat(view).isEqualTo("tasks/edit");
        assertThat(actualTask).isEqualTo(task1);
        assertThat(priorities).isEqualTo(List.of(priority));
    }

    @Test
    public void whenRequestEditPageEndTaskNotFound() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null, null);
        when(taskService.findById(any(Integer.class))).thenReturn(null);

        var model = new ConcurrentModel();
        var view = taskController.edit(model, task1.getId());
        var errorMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(errorMessage).isEqualTo("Not found task by ID: " + task1.getId());
    }

    @Test
    public void whenDeleteTask() {
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null, null);
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
        var task1 = new Task(0, "Task1", LocalDateTime.now(), false, null, null);
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