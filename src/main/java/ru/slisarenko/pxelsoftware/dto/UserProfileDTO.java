package ru.slisarenko.pxelsoftware.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileDTO {
    private Long id;
    private String name;
    private String password;
    private LocalDate dateOfBirth;
    private BigDecimal balance;
    @Builder.Default
    private List<String> emails = new ArrayList<>();
    @Builder.Default
    private List<String> phones = new ArrayList<>();
    private String messageError;
}
