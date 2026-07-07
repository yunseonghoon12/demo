package com.example.demo.login;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

//JWT 인증을 담당하는 필터
//OncePerRequestFilter > 한 요청(Request)에 딱 한 번만 실행된다
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    static final String ATTR_USERNAME = "authenticatedUsername";
    static final String ATTR_USER_ID = "authenticatedUserId";

    private final JwtTokenProvider jwtTokenProvider;
    private final String cookieName;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, String cookieName) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.cookieName = cookieName;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();

        return ("POST".equalsIgnoreCase(method) && "/login".equals(path))
            || ("GET".equalsIgnoreCase(method) && "/".equals(path))
            || path.startsWith("/ws")
            || path.equals("/chat.html"); //채팅 테스트 페이지는 인증 없이 접근 가능
    }


// 모든 인증은 이 메서드에서 이루어짐
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            sendUnauthorized(response);
            return;
        }

        request.setAttribute(ATTR_USERNAME, jwtTokenProvider.getUsername(token));
        request.setAttribute(ATTR_USER_ID, jwtTokenProvider.getUserId(token));
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length()).trim();
        }

        return null;
    }

    private void sendUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"message\":\"인증이 필요합니다.\"}");
    }
}
