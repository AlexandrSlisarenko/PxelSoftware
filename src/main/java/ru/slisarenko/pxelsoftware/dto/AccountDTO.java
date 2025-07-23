package ru.slisarenko.pxelsoftware.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDTO {
    private BigDecimal balance;
    private BigDecimal startBalance;
    private Integer interestRate;
}
