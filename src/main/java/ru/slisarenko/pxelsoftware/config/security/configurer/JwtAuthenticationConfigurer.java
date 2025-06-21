package ru.slisarenko.pxelsoftware.config.security.configurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.slisarenko.pxelsoftware.security.converter.JwtAuthenticationConverter;
import ru.slisarenko.pxelsoftware.security.factory.AccessTokenFactory;
import ru.slisarenko.pxelsoftware.security.factory.RefreshTokenFactory;
import ru.slisarenko.pxelsoftware.security.filter.JwtLogoutFilter;
import ru.slisarenko.pxelsoftware.security.filter.RefreshTokenFilter;
import ru.slisarenko.pxelsoftware.security.filter.RequestJwtTokenFilter;
import ru.slisarenko.pxelsoftware.security.repository.JdbcTokenLogoutRepository;
import ru.slisarenko.pxelsoftware.security.serialization.AccessTokenJwsStringDeserialize;
import ru.slisarenko.pxelsoftware.security.serialization.AccessTokenStringSerialization;
import ru.slisarenko.pxelsoftware.security.serialization.RefreshTokenStringDeserializer;
import ru.slisarenko.pxelsoftware.security.serialization.RefreshTokenStringSerializer;
import ru.slisarenko.pxelsoftware.security.service.TokenAuthenticationUserDetailsService;

import java.util.Objects;

@Slf4j
@Builder
@RequiredArgsConstructor
public class JwtAuthenticationConfigurer extends AbstractHttpConfigurer<JwtAuthenticationConfigurer, HttpSecurity> {

    private final String pathRequestToken;
    private final String pathRefreshToken;
    private final String pathLogoutToken;

    private final RefreshTokenFactory refreshTokenFactory;
    private final AccessTokenFactory accessTokenFactory;

    private final AccessTokenJwsStringDeserialize accessTokenStringDeserializer;
    private final RefreshTokenStringDeserializer refreshTokenStringDeserializer;
    private final RefreshTokenStringSerializer refreshTokenStringSerializer;
    private final AccessTokenStringSerialization accessTokenStringSerializer;

    private final JdbcTokenLogoutRepository jwtTokenLogoutRepository;

    @Override
    public void init(HttpSecurity builder) throws Exception {
        super.init(builder);
        var csrfConfigurer = builder.getConfigurer(CsrfConfigurer.class);
        var requestMatcher = new RegexRequestMatcher(pathRequestToken, RequestMethod.POST.name());

        if (Objects.nonNull(csrfConfigurer)) {
            csrfConfigurer.ignoringRequestMatchers(requestMatcher);
        }
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        super.configure(builder);

        var provider = createProvider();

        var requestJwtTokenFilter = createJwtTokenFilter();
        var jwtAuthenticationFilter = createJwtAuthenticationFilter(builder);
        var refreshTokenFilter = createJwtRefreshTokenFilter();
        var jwtLogoutFilter = createJwtLogoutFilter();


        builder.addFilterAfter(requestJwtTokenFilter, ExceptionTranslationFilter.class)
                .addFilterAfter(refreshTokenFilter, ExceptionTranslationFilter.class)
                .addFilterAfter(jwtLogoutFilter, ExceptionTranslationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, CsrfFilter.class)
                .authenticationProvider(provider);
    }

    private PreAuthenticatedAuthenticationProvider createProvider() {
        var provider = new PreAuthenticatedAuthenticationProvider();
        var service = new TokenAuthenticationUserDetailsService(this.jwtTokenLogoutRepository);
        provider.setPreAuthenticatedUserDetailsService(service);
        return provider;
    }

    private RequestJwtTokenFilter createJwtTokenFilter() {
        return RequestJwtTokenFilter.builder()
                .requestMatcher(createRequestMatcher(this.pathRequestToken))
                .securityContextRepository(new RequestAttributeSecurityContextRepository())
                .accessTokenFactory(this.accessTokenFactory)
                .refreshTokenFactory(this.refreshTokenFactory)
                .accessTokenStringSerializer(this.accessTokenStringSerializer)
                .refreshTokenStringSerializer(this.refreshTokenStringSerializer)
                .objectMapper(new ObjectMapper())
                .build();
    }

    private RequestMatcher createRequestMatcher(String path) {
        return new RegexRequestMatcher(path, HttpMethod.POST.name());
    }

    private AuthenticationFilter createJwtAuthenticationFilter(HttpSecurity builder) {

        var authenticationManager =
                builder.getSharedObject(AuthenticationManager.class);
        var authenticationConverter =
                new JwtAuthenticationConverter(this.accessTokenStringDeserializer, this.refreshTokenStringDeserializer);
        var jwtAuthenticationFilter =
                new AuthenticationFilter(authenticationManager, authenticationConverter);

        jwtAuthenticationFilter
                .setSuccessHandler((request, response, authentication) ->
                        CsrfFilter.skipRequest(request));
        jwtAuthenticationFilter
                .setFailureHandler((request, response, exception) ->
                        response.sendError(HttpServletResponse.SC_FORBIDDEN));
        jwtAuthenticationFilter
                .setSecurityContextRepository(new RequestAttributeSecurityContextRepository());
        jwtAuthenticationFilter
                .setSecurityContextHolderStrategy(SecurityContextHolder.getContextHolderStrategy());

        return jwtAuthenticationFilter;
    }

    private RefreshTokenFilter createJwtRefreshTokenFilter() {
        return RefreshTokenFilter.builder()
                .accessTokenFactory(this.accessTokenFactory)
                .accessTokenStringSerializer(this.accessTokenStringSerializer)
                .requestMatcher(createRequestMatcher(this.pathRefreshToken))
                .securityContextRepository(new RequestAttributeSecurityContextRepository())
                .objectMapper(new ObjectMapper())
                .build();
    }

    private JwtLogoutFilter createJwtLogoutFilter() {
        return JwtLogoutFilter.builder()
                .jdbcTokenLogoutRepository(this.jwtTokenLogoutRepository)
                .requestMatcher(createRequestMatcher(this.pathLogoutToken))
                .securityContextRepository(new RequestAttributeSecurityContextRepository())
                .build();
    }
}
