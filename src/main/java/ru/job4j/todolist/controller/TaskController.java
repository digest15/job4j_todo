package ru.job4j.todolist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todolist.model.Task;
import ru.job4j.todolist.service.TaskService;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "tasks/list";
    }

    @GetMapping("/done")
    public String getAllDone(Model model) {
        try {
            model.addAttribute("tasks", taskService.findAll(true));
            return "tasks/listDone";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("/new")
    public String getAllNew(Model model) {
        try {
            model.addAttribute("tasks", taskService.findAll(false));
            return "tasks/listNew";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("/create")
    public String getCreationPage() {
        return "tasks/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Task task, Model model) {
        try {
            taskService.add(task);
            return "redirect:/tasks";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
    }

    @PostMapping("/done/{isDone}")
    public String setDone(@ModelAttribute Task task, @PathVariable Boolean isDone,  Model model) {
        try {
            taskService.doneTask(task, isDone);
            model.addAttribute("task", task);
            return "redirect:/tasks/" + task.getId();
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Task task,  Model model) {
        try {
            taskService.update(task);
            model.addAttribute("task", task);
            return "redirect:/tasks/" + task.getId();
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        try {
            model.addAttribute("task", taskService.findById(id));
            return "tasks/one";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("edit/{id}")
    public String edit(Model model, @PathVariable int id) {
        try {
            model.addAttribute("task", taskService.findById(id));
            return "tasks/edit";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        try {
            taskService.delete(taskService.findById(id));
            return "redirect:/tasks";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "errors/404";
        }
    }

}
