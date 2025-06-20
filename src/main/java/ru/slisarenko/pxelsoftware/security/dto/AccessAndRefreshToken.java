package ru.slisarenko.pxelsoftware.security.dto;

import lombok.Builder;

@Builder
public record AccessAndRefreshToken(String accessToken,
                                    String expiresAtAccessToken,
                                    String refreshToken,
                                    String expiresAtRefreshToken) {
}
