package com.example.demo.user;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class UserDataInitializer implements ApplicationRunner {
                                            //Spring Boot가 완전히 실행된 직후 한 번 실행되는 클래스
    private final UserRepository userRepository;

    public UserDataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
