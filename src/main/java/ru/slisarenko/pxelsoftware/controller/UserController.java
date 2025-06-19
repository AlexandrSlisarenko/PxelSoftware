package ru.slisarenko.pxelsoftware.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to PxelSoftware";
    }

    @GetMapping("/user/userProfile")
    public String userProfile() {
        return "User Profile";
    }
}
