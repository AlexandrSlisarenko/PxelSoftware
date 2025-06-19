package ru.slisarenko.pxelsoftware.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class UserFilterByNameAndPhoneAndEmailAndDateOfBirth implements FilterParams{
    private String name;
    private String phone;
    private String email;
    private LocalDate dateOfBirth;
}
