package ru.slisarenko.pxelsoftware.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "email_data" , schema = "pixel")
public class EmailData extends BaseEntity<Long> {

    @Column(name = "email" , nullable = false, length = 200, unique = true)
    @Email(message = "Incorrect message")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
