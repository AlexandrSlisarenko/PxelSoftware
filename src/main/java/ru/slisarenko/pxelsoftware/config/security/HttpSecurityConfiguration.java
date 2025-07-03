package ru.slisarenko.pxelsoftware.config.security;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import ru.slisarenko.pxelsoftware.config.security.configurer.JwtAuthenticationConfigurer;
import ru.slisarenko.pxelsoftware.security.factory.AccessTokenFactory;
import ru.slisarenko.pxelsoftware.security.factory.RefreshTokenFactory;
import ru.slisarenko.pxelsoftware.security.repository.JdbcTokenLogoutRepository;
import ru.slisarenko.pxelsoftware.security.repository.JdbcTokenLogoutRepositoryImpl;
import ru.slisarenko.pxelsoftware.security.repository.JdbcUserDetailRepositoryImpl;
import ru.slisarenko.pxelsoftware.security.serialization.AccessTokenJwsStringDeserialize;
import ru.slisarenko.pxelsoftware.security.serialization.AccessTokenStringSerialization;
import ru.slisarenko.pxelsoftware.security.serialization.RefreshTokenStringDeserializer;
import ru.slisarenko.pxelsoftware.security.serialization.RefreshTokenStringSerializer;

import javax.sql.DataSource;
import java.text.ParseException;
import java.time.Duration;


@Slf4j
@Configuration
@EnableWebSecurity
public class HttpSecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailRepositoryImpl(dataSource);
    }

    @Bean
    public JdbcTokenLogoutRepository jdbcTokenLogoutRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcTokenLogoutRepositoryImpl(jdbcTemplate);
    }

    @Bean
    public AccessTokenStringSerialization accessTokenStringSerialization(
            @Value("${jwt.access-token-key}") String accessTokenKey
    ) throws KeyLengthException, ParseException {
        var accessSigner = new MACSigner(OctetSequenceKey.parse(accessTokenKey));
        return new AccessTokenStringSerialization(accessSigner);
    }

    @Bean
    public AccessTokenJwsStringDeserialize accessTokenJwsStringDeserialize(
            @Value("${jwt.access-token-key}") String accessTokenKey
    ) throws ParseException, JOSEException{
        var verifier = new MACVerifier(OctetSequenceKey.parse(accessTokenKey));
        return new AccessTokenJwsStringDeserialize(verifier);
    }

    @Bean
    public RefreshTokenStringSerializer refreshTokenJwsStringSerialize(
            @Value("${jwt.refresh-token-key}") String refreshTokenKey
    ) throws ParseException, KeyLengthException {
        var refreshSigner = new DirectEncrypter(OctetSequenceKey.parse(refreshTokenKey));
        return new RefreshTokenStringSerializer(refreshSigner);
    }

    @Bean
    public RefreshTokenStringDeserializer refreshTokenJwsStringDeserialize(
            @Value("${jwt.refresh-token-key}") String refreshTokenKey
    ) throws ParseException, KeyLengthException {
        var decrypter = new DirectDecrypter(OctetSequenceKey.parse(refreshTokenKey));
        return new RefreshTokenStringDeserializer(decrypter);
    }

    @Bean
    public AccessTokenFactory accessTokenFactory(
            @Value("${jwt.duration-access-interval-min}") Long durationAccessIntervalMin
    ){
        return new AccessTokenFactory(Duration.ofMinutes(durationAccessIntervalMin));
    }

    @Bean
    public RefreshTokenFactory refreshTokenFactory(
            @Value("${jwt.duration-refresh-interval-hours}") Long durationRefreshIntervalHours
    ){
        return new RefreshTokenFactory(Duration.ofHours(durationRefreshIntervalHours));
    }

    @Bean
    public RequestAttributeSecurityContextRepository requestAttributeSecurityContextRepository(){
        return new RequestAttributeSecurityContextRepository();
    }

    @Bean
    public JwtAuthenticationConfigurer jwtAuthenticationConfigurer(
            @Value("${jwt.request-path}") String requestPath,
            @Value("${jwt.refresh-path}") String refreshPath,
            @Value("${jwt.logout-path}") String logoutPath,
            RefreshTokenFactory refreshTokenFactory,
            AccessTokenFactory accessTokenFactory,
            AccessTokenStringSerialization accessTokenStringSerialization,
            AccessTokenJwsStringDeserialize accessTokenJwsStringDeserialize,
            RefreshTokenStringDeserializer refreshTokenJwsStringDeserialize,
            RefreshTokenStringSerializer refreshTokenJwsStringSerialize,
            JdbcTokenLogoutRepository jdbcTokenLogoutRepository,
            RequestAttributeSecurityContextRepository securityContextRepository
    ){
        return JwtAuthenticationConfigurer.builder()
                .pathRequestToken(requestPath)
                .pathLogoutToken(logoutPath)
                .pathRefreshToken(refreshPath)
                .jwtTokenLogoutRepository(jdbcTokenLogoutRepository)
                .refreshTokenFactory(refreshTokenFactory)
                .accessTokenFactory(accessTokenFactory)
                .refreshTokenStringSerializer(refreshTokenJwsStringSerialize)
                .refreshTokenStringDeserializer(refreshTokenJwsStringDeserialize)
                .accessTokenStringSerializer(accessTokenStringSerialization)
                .accessTokenStringDeserializer(accessTokenJwsStringDeserialize)
                .requestAttributeSecurityContextRepository(securityContextRepository)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http,
                                                   JwtAuthenticationConfigurer jwtAuthenticationConfigurer) throws Exception {
        http.apply(jwtAuthenticationConfigurer);
        http.httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorizeRequest ->
                        authorizeRequest
                                .requestMatchers(HttpMethod.POST, "/auth/welcome").hasRole("USER")
                                /* .requestMatchers(HttpMethod.POST, "/public/**").hasRole("ADMIN")*/
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .anyRequest().authenticated()
                ).sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();

    }
}
