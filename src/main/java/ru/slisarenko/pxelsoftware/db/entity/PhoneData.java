package ru.slisarenko.pxelsoftware.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "phone_data" , schema = "pixel")
public class PhoneData extends BaseEntity<Long> {

    @Column(name = "phone" , nullable = false, length = 11, unique = true)
    @Pattern(regexp = "^\\d{11}$", message = "Incorrect phone")
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
