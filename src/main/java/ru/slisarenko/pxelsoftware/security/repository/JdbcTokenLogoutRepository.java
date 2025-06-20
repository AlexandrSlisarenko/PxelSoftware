package ru.slisarenko.pxelsoftware.security.repository;

import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.UUID;

@Repository
public interface JdbcTokenLogoutRepository {

    Boolean isDeactivatedToken(UUID id);

    void insertDeactivatedToken(UUID id, Date c_keep_until);

    String getPassword(String username);
}

