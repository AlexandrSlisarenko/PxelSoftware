package ru.slisarenko.pxelsoftware.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.slisarenko.pxelsoftware.dto.TransferDTO;
import ru.slisarenko.pxelsoftware.dto.UserDTO;
import ru.slisarenko.pxelsoftware.exception.UserException;
import ru.slisarenko.pxelsoftware.security.authentication.IAuthenticationFacade;
import ru.slisarenko.pxelsoftware.service.UserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final IAuthenticationFacade authenticationFacade;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to PxelSoftware";
    }

    @GetMapping("/user/profile")
    public UserDTO userProfile() {
        return userService.getByName(authenticationFacade.getUsername());
    }

    @PostMapping(value = "/transfer/", consumes = "application/json", produces = "application/json")
    public String transfer(@RequestBody TransferDTO transferData) throws UserException {
        return userService.protectedTransfer(authenticationFacade.getUsername(), transferData);
    }
}
