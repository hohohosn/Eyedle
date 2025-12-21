package com.chatservice.infra.security;

import com.common.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.chatservice.common.ChatErrorCode.INVALID_USER_ID_FORMAT;
import static com.chatservice.common.ChatErrorCode.INVALID_USER_ID_HEADER;

@Slf4j
@Component
public class GatewayHeaderAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return !path.startsWith("/chats") && path.startsWith("/chat");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
  ) throws ServletException, IOException {

    String userIdHeader = request.getHeader("X-User-Id");
    String userRoleHeader = request.getHeader("X-User-Role");

    if (!StringUtils.hasText(userIdHeader)) {
      throw new CustomException(INVALID_USER_ID_HEADER);
    }

    long userId;
    try {
      userId = Long.parseLong(userIdHeader);

    } catch (NumberFormatException e) {
      throw new CustomException(INVALID_USER_ID_FORMAT);
    }

    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(userRoleHeader));
    Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
    log.info("인증 객체 설정 완료: userId={}, authorities={}", userId, authorities);

    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);
  }
}
