package com.padmavathi.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/", "/index.html"})
    public String home() {
        return "index";
    }

    @GetMapping({"/admin", "/admin.html"})
    public String admin() {
        return "admin";
    }
}
