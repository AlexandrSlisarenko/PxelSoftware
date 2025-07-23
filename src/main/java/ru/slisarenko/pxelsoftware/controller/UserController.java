package ru.slisarenko.pxelsoftware.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.slisarenko.pxelsoftware.dto.AccountDTO;
import ru.slisarenko.pxelsoftware.dto.UpdateParamentDTO;
import ru.slisarenko.pxelsoftware.dto.UserProfileDTO;
import ru.slisarenko.pxelsoftware.security.authentication.IAuthenticationFacade;
import ru.slisarenko.pxelsoftware.service.UserService;

@RestController
@RequestMapping("/auth/user")
@RequiredArgsConstructor
@Tag(name = "Личный кабинет", description = "Все инструмнты по работе с личными данными")
public class UserController {

    private final UserService userService;
    private final IAuthenticationFacade authenticationFacade;

    @Operation(summary = "Профиль пользователя", description = "username, birthday, emails, phones")
    @GetMapping("/profile")
    public UserProfileDTO userProfile() {
        return userService.getByName(authenticationFacade.getUsername());
    }

    @Operation(summary = "Информация о финансах", description = "balance startBalance interestRate")
    @GetMapping("/account")
    public AccountDTO userAccount() {
        return userService.getAccountByName(authenticationFacade.getUsername());
    }

    @Operation(summary = "Добавить Emil")
    @PostMapping("/email")
    public UserProfileDTO addEmail(@Email @RequestBody String email) {
        return userService.addEmail(getUserId(), email);
    }

    @Operation(summary = "Измени Emil")
    @PutMapping("/email")
    public UserProfileDTO editEmail(@RequestBody UpdateParamentDTO updateEmail) {
        return userService.updateEmail(getUserId(), updateEmail.oldParam(), updateEmail.newParam());
    }

    @Operation(summary = "Удалить Emil")
    @DeleteMapping("/email")
    public UserProfileDTO deleteEmail(@Email @RequestBody String email) {
        return userService.deleteEmail(getUserId(), email);
    }

    @PostMapping("/phone")
    public UserProfileDTO addPhone(@RequestBody String phone) {
        return userService.addEmail(getUserId(), phone);
    }

    @PutMapping("/phone")
    public UserProfileDTO editPhone(@RequestBody String phoneOld, @RequestBody String phoneNew) {
        return userService.updateEmail(getUserId(), phoneOld, phoneNew);
    }

    @DeleteMapping("/phone")
    public UserProfileDTO deletePhone(@RequestBody String phone) {
        return userService.deleteEmail(getUserId(), phone);
    }


    private Long getUserId() {
        return userService.getIdByName(authenticationFacade.getUsername());
    }

}
