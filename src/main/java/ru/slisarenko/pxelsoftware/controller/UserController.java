package ru.slisarenko.pxelsoftware.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.slisarenko.pxelsoftware.dto.UserDTO;
import ru.slisarenko.pxelsoftware.service.UserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to PxelSoftware";
    }

    @GetMapping("/user/{id}")
    public UserDTO userProfile(@PathVariable Long id) {
        return userService.getById(id);
    }
}
