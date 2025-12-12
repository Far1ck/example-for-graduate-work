package ru.skypro.homework.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder, UserMapper mapper) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.mapper = mapper;
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

    public User getUser(String username) {
        UserEntity user = userRepository.findByEmail(username);
        return mapper.toDto(user);
    }

    public UpdateUser updateUser(String username, UpdateUser updateUser) {
        UserEntity user = userRepository.findByEmail(username);
        user.setFirstName(updateUser.getFirstName());
        user.setLastName(updateUser.getLastName());
        user.setPhone(updateUser.getPhone());
        userRepository.save(user);
        return updateUser;
    }
}
