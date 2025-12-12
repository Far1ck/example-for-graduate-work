package ru.skypro.homework.service;

import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;

public interface UserService {
    void createUser(UserEntity user);
    boolean setPassword(String username, NewPassword passwords);
    User getUser(String username);
    UpdateUser updateUser(String name, UpdateUser updateUser);
}
