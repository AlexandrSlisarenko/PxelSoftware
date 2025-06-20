package ru.slisarenko.pxelsoftware.security.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
public class JdbcTokenLogoutRepositoryImpl implements JdbcTokenLogoutRepository {
    private static final String INSERT_TIME_DEACTIVATED_TOKEN = """
            INSERT INTO pixel.deactivated_token(id, keep_until) VALUES (?, ?);
            """;

    private static final String SELECT_DEACTIVATED_TOKEN_ID = """
            SELECT EXISTS (SELECT token.id FROM pixel.deactivated_token token WHERE token.id = ?);
            """;

    private static final String SELECT_USER_PASSWORD = """
            SELECT us.user_password
            FROM pixel.users us
            WHERE us.user_name = ?
            """;

    private final JdbcTemplate jdbcTemplate;



    @Override
    public Boolean isDeactivatedToken(UUID id) {
        return jdbcTemplate.queryForObject(SELECT_DEACTIVATED_TOKEN_ID, Boolean.class, id);
    }

    @Override
    public void insertDeactivatedToken(UUID id, Date c_keep_until) {
        jdbcTemplate.update(INSERT_TIME_DEACTIVATED_TOKEN, id, c_keep_until);
    }

    @Override
    public String getPassword(String username) {
        return  jdbcTemplate.queryForObject(SELECT_USER_PASSWORD, String.class, username);
    }
}
