package ru.skypro.homework.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public void createUser(UserEntity user) {
        userRepository.save(user);
    };

    public boolean setPassword(String username, NewPassword password) {
        UserEntity user = userRepository.findByEmail(username);
        if (!encoder.matches(password.getCurrentPassword(), user.getPassword())) {
            return false;
        }
        user.setPassword(encoder.encode(password.getNewPassword()));
        userRepository.save(user);
        return true;
    }
}
