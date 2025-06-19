package ru.slisarenko.pxelsoftware.db.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(exclude = {"account", "emails", "phones"})
@EqualsAndHashCode(exclude = {"account", "emails", "phones"}, callSuper = true)
@Entity
@Table(name = "users", schema = "pixel")
public class User extends BaseEntity<Long> {

    @Column(name = "user_name", nullable = false, length = 500, unique = true)
    @Length(max = 500)
    private String name;

    @Column(name = "date_of_birth", nullable = false)
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate dateOfBirth;

    @Column(name = "user_password", nullable = false, length = 500, unique = true)
    @Length(min = 8, max = 500)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Account account;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<EmailData> emails = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PhoneData> phones = new ArrayList<>();
}
