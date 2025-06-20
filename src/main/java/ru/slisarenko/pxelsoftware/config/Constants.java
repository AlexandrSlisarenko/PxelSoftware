package ru.slisarenko.pxelsoftware.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String PREFIX_ROLE_USER = "GRAND_";
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";
    public static final String CLAIM_AUTHORITIES = "authorities";
    public static final String JWT_REFRESH = "JWT_REFRESH";
    public static final String JWT_LOGOUT = "JWT_LOGOUT";
}
