package com.example.librarymanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HomeController {
    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
            "application", "Library Management System",
            "status", "running",
            "endpoints", Map.of(
                "books", "/api/books",
                "members", "/api/members",
                "loans", "/api/loans",
                "swaggerUi", "/swagger-ui/index.html"
            ),
            "tip", "Use VS Code terminal or an API client to send POST, PUT, and DELETE requests."
        );
    }
}
