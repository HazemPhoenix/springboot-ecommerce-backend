package io.spring.training.boot.server.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorController authorService;
}
