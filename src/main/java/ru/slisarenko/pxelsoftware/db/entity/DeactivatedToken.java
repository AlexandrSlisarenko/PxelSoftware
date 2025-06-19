package ru.slisarenko.pxelsoftware.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "deactivated_token", schema = "pixel")
public class DeactivatedToken {
    
    @Id
    private UUID id;

    @Column(name = "keep_until")
    private LocalDateTime keepUntil;
}
