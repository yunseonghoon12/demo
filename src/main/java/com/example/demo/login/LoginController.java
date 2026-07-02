package com.example.demo.login;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class LoginController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return userService.login(request.getUsername(), request.getPassword())
                .map(user -> {
                    response.addHeader(HttpHeaders.SET_COOKIE, jwtTokenProvider.createTokenCookie(user).toString());
                    return "login-success";
                })
                .orElse("login-fail");
    }
}
