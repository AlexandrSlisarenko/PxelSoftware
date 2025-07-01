package ru.slisarenko.pxelsoftware.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "account" , schema = "pixel")
@OptimisticLocking(type = OptimisticLockType.VERSION)
public class Account extends BaseEntity<Long>{

    @DecimalMin("0")
    @Column(name = "balance", precision = 5)
    private BigDecimal balance;

    @DecimalMin("0")
    @Column(name = "start_balance", precision = 5)
    private BigDecimal startBalance;

    @DecimalMax("20")
    @Column(name = "interest_rate", precision = 5)
    private Integer interestRate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Version
    @Column(name = "version")
    private Long version;
}

