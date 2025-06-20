package ru.slisarenko.pxelsoftware.security.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record Token(UUID id,
                    String username,
                    List<String> authorities,
                    Instant createdAt,
                    Instant expiresAt) {
}
