package ru.slisarenko.pxelsoftware.security.filter;

import com.fasterxml.jackson.core.filter.TokenFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.slisarenko.pxelsoftware.security.dto.AccessAndRefreshToken;
import ru.slisarenko.pxelsoftware.security.dto.Token;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;
import java.util.function.Function;

@RequiredArgsConstructor
@Builder
public class RequestJwtTokenFilter extends OncePerRequestFilter {

    private final RequestMatcher requestMatcher;

    private final SecurityContextRepository securityContextRepository;

    private final Function<Authentication, Token> refreshTokenFactory;

    private final Function<Token, Token> accessTokenFactory;

    private final Function<Token, String> refreshTokenStringSerializer;

    private final Function<Token, String> accessTokenStringSerializer;

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (this.requestMatcher.matches(request)) {
            if (this.securityContextRepository.containsContext(request)) {
                var context = this.securityContextRepository.loadDeferredContext(request).get();
                if (Objects.nonNull(context) && !(context.getAuthentication() instanceof PreAuthenticatedAuthenticationToken)) {
                    var refreshToken = this.refreshTokenFactory.apply(context.getAuthentication());
                    var accessToken = this.accessTokenFactory.apply(refreshToken);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    this.objectMapper.writeValue(response.getWriter(),
                            AccessAndRefreshToken.builder()
                                    .accessToken(this.accessTokenStringSerializer.apply(accessToken))
                                    .expiresAtAccessToken(accessToken.expiresAt().toString())
                                    .refreshToken(this.refreshTokenStringSerializer.apply(refreshToken))
                                    .expiresAtRefreshToken(refreshToken.expiresAt().toString())
                                    .build());

                    return;
                }
            }

            throw new AccessDeniedException("User mast be authenticated");
        }

        filterChain.doFilter(request, response);

    }
}
