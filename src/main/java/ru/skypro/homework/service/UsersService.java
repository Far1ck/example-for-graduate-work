package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;

import java.io.IOException;

public interface UsersService {
    void createUser(UserEntity user);
    boolean setPassword(String username, NewPassword passwords);
    User getUser(String username);
    UpdateUser updateUser(String name, UpdateUser updateUser);
    void updateUserImage(String name, MultipartFile image) throws IOException;
}
