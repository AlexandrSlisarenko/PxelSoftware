package ru.slisarenko.pxelsoftware.config;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String PREFIX_ROLE_USER = "GRAND_";
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";
    public static final String CLAIM_AUTHORITIES = "authorities";
    public static final String JWT_REFRESH = "JWT_REFRESH";
    public static final String JWT_LOGOUT = "JWT_LOGOUT";

    public static final JWSAlgorithm JWS_ALGORITHM_SERIALIZATION = JWSAlgorithm.HS384;
    public static final JWEAlgorithm JWE_ALGORITHM_SERIALIZATION = JWEAlgorithm.DIR;
    public static final EncryptionMethod ENCRYPTION_METHOD_SERIALIZATION = EncryptionMethod.A192CBC_HS384;

    public static final String UPDATE_DEPOSIT_JOB = "updateDepositJobBeen";
    public static final String UPDATE_STATE_DEPOSIT_JOB = "updateStateDepositJobBeen";
    public static final String TRIGGER_UPDATE_DEPOSIT_JOB = "updateDepositJobTrigger";
    public static final String TRIGGER_UPDATE_STATE_DEPOSIT_JOB = "updateStateDepositJobTrigger";

    public static final Boolean SEND = true;
    public static final Boolean ACCEPT = false;
    public static final String TRANSFER_COMPLETED = "TRANSFER_COMPLETED";
}
