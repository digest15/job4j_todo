package ru.job4j.todolist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(value = "/")
public class IndexController {
    @GetMapping("/index")
    public String getIndex() {
        return "index";
    }
}
