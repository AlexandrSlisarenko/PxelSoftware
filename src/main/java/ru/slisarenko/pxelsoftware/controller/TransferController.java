package ru.slisarenko.pxelsoftware.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.slisarenko.pxelsoftware.dto.TransferDTO;
import ru.slisarenko.pxelsoftware.dto.UserProfileDTO;
import ru.slisarenko.pxelsoftware.dto.filter.UserFilterByNameAndPhoneAndEmailAndDateOfBirth;
import ru.slisarenko.pxelsoftware.exception.UserException;
import ru.slisarenko.pxelsoftware.security.authentication.IAuthenticationFacade;
import ru.slisarenko.pxelsoftware.service.UserService;

@RestController
@RequestMapping("/auth/transfer")
@RequiredArgsConstructor
public class TransferController {
    private final UserService userService;
    private final IAuthenticationFacade authenticationFacade;

    @PostMapping(value = "/send", consumes = "application/json", produces = "application/json")
    public String transfer(@RequestBody TransferDTO transferData) throws UserException {
        return userService.protectedTransfer(authenticationFacade.getUsername(), transferData);
    }

    @GetMapping("/list")
    public Page<UserProfileDTO> userList(@RequestBody UserFilterByNameAndPhoneAndEmailAndDateOfBirth userFilter,
                                         Pageable pageable) {
        return userService.searchByFilter(userFilter, pageable);
    }
}
