package com.example.demo.login;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(User::isEnabled)
                .filter(user -> user.getPassword().equals(password));
    }
}
