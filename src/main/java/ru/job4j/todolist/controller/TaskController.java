package ru.job4j.todolist.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.model.User;
import ru.job4j.todolist.service.PriorityService;
import ru.job4j.todolist.service.TaskService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    private final PriorityService priorityService;

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "tasks/list";
    }

    @GetMapping("/done")
    public String getAllDone(Model model) {
        model.addAttribute("tasks", taskService.findAll(true));
        return "tasks/listDone";
    }

    @GetMapping("/new")
    public String getAllNew(Model model) {
        model.addAttribute("tasks", taskService.findAll(false));
        return "tasks/listNew";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model) {
        model.addAttribute("priorities", priorityService.findAll());
        return "tasks/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Task task, Model model, HttpSession session) {
        task.setUser((User) session.getAttribute("user"));
        Task newTask = taskService.add(task);

        if (newTask == null) {
            model.addAttribute("message", "Error when save Task " + task);
            return "errors/404";
        }
        return "redirect:/tasks";
    }

    @PostMapping("/done/{isDone}")
    public String setDone(@ModelAttribute Task task, @PathVariable Boolean isDone, Model model) {
        if (!taskService.doneTask(task, isDone)) {
            model.addAttribute("message", "Error when update task " + task);
            return "errors/404";
        }
        model.addAttribute("task", task);
        return "redirect:/tasks/" + task.getId();
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Task task, Model model) {
        if (!taskService.update(task)) {
            model.addAttribute("message", "Error when update task " + task);
            return "errors/404";
        }
        model.addAttribute("task", task);
        return "redirect:/tasks/" + task.getId();
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        Task task = taskService.findById(id);
        if (task == null) {
            model.addAttribute("message", "Not found task by ID: " + id);
            return "errors/404";
        }
        model.addAttribute("task", task);
        return "tasks/one";
    }

    @GetMapping("edit/{id}")
    public String edit(Model model, @PathVariable int id) {
        Task task = taskService.findById(id);
        if (task == null) {
            model.addAttribute("message", "Not found task by ID: " + id);
            return "errors/404";
        }
        model.addAttribute("priorities", priorityService.findAll());
        model.addAttribute("task", task);
        return "tasks/edit";
    }

    @GetMapping("delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        if (!taskService.delete(id)) {
            model.addAttribute("message", "Error when delete Task for id: " + id);
            return "errors/404";
        }
        return "redirect:/tasks";
    }

}
