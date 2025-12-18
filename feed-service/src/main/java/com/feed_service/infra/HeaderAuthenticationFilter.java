package com.feed_service.infra;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_ROLE = "X-User-Role";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String userIdHeader = request.getHeader(HEADER_USER_ID);
        String roleHeader = request.getHeader(HEADER_USER_ROLE);

        if (StringUtils.hasText(userIdHeader)) {
            try {
                Long userId = Long.parseLong(userIdHeader);

                List<SimpleGrantedAuthority> authorities =
                        StringUtils.hasText(roleHeader)
                                ? List.of(new SimpleGrantedAuthority(roleHeader))
                                : List.of();

                Authentication auth =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                authorities
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (NumberFormatException e) {
                log.warn("Invalid user ID format in X-User-Id header: {}", userIdHeader);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}