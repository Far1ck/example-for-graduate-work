package ru.skypro.homework.service;

import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.entity.UserEntity;

public interface UserService {
    void createUser(UserEntity user);
    boolean setPassword(String username, NewPassword passwords);
}
