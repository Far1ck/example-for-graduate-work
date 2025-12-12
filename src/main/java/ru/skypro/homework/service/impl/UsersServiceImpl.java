package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UsersRepository;
import ru.skypro.homework.service.UsersService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder encoder;
    private final UserMapper mapper;
    @Value("${app.avatar.dir}")
    private String avatarDirectoryPath;

    public UsersServiceImpl(UsersRepository usersRepository, PasswordEncoder encoder, UserMapper mapper) {
        this.usersRepository = usersRepository;
        this.encoder = encoder;
        this.mapper = mapper;
    }

    public void createUser(UserEntity user) {
        usersRepository.save(user);
    };

    public boolean setPassword(String username, NewPassword password) {
        UserEntity user = usersRepository.findByEmail(username);
        if (!encoder.matches(password.getCurrentPassword(), user.getPassword())) {
            return false;
        }
        user.setPassword(encoder.encode(password.getNewPassword()));
        usersRepository.save(user);
        return true;
    }

    public User getUser(String username) {
        UserEntity user = usersRepository.findByEmail(username);
        return mapper.toDto(user);
    }

    public UpdateUser updateUser(String username, UpdateUser updateUser) {
        UserEntity user = usersRepository.findByEmail(username);
        user.setFirstName(updateUser.getFirstName());
        user.setLastName(updateUser.getLastName());
        user.setPhone(updateUser.getPhone());
        usersRepository.save(user);
        return updateUser;
    }

    public void updateUserImage(String name, MultipartFile image) throws IOException {
        Path avatarDirectory = Paths.get(avatarDirectoryPath);
        UserEntity user = usersRepository.findByEmail(name);
        Files.createDirectories(avatarDirectory);
        String extension = getFileExtension(image.getOriginalFilename());
        String fileName = this.getClass().getSimpleName() + user.getId() + extension;
        Path filePath = avatarDirectory.resolve(fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        user.setImage("/images/" + fileName);
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.'));
    }
}
