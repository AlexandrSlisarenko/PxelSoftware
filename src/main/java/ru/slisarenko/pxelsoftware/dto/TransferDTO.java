package ru.slisarenko.pxelsoftware.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransferDTO {
    private Long acceptingUserId;
    private Double transferAmount;
}
