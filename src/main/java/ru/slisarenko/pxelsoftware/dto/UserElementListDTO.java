package ru.slisarenko.pxelsoftware.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserElementListDTO {
    private String userName;
    @Builder.Default
    private List<String> emails = new ArrayList<>();
    @Builder.Default
    private List<String> phones = new ArrayList<>();
}
