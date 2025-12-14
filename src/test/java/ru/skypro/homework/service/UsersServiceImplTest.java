package ru.skypro.homework.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UsersRepository;
import ru.skypro.homework.service.impl.UsersServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UsersServiceImpl usersService;

    private UserEntity userEntity;
    private UpdateUser updateUser;
    private NewPassword newPassword;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        // Инициализация тестовых данных
        userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");
        userEntity.setPhone("+79991234567");
        userEntity.setPassword("encodedPassword");
        userEntity.setImage("/images/old.jpg");

        updateUser = new UpdateUser();
        updateUser.setFirstName("Jane");
        updateUser.setLastName("Smith");
        updateUser.setPhone("+79997654321");

        newPassword = new NewPassword();
        newPassword.setCurrentPassword("oldPassword");
        newPassword.setNewPassword("newPassword123");

        mockFile = new MockMultipartFile(
                "image",
                "avatar.png",
                "image/png",
                "fake image data".getBytes()
        );
    }

    @Test
    void createUser_shouldSaveUser() throws Exception {
        usersService.createUser(userEntity);
        verify(usersRepository).save(userEntity);
    }

    @Test
    void setPassword_shouldReturnTrue_whenCurrentPasswordValid() {
        when(usersRepository.findByEmail("test@example.com")).thenReturn(userEntity);
        when(encoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(encoder.encode("newPassword123")).thenReturn("newEncodedPassword");

        boolean result = usersService.setPassword("test@example.com", newPassword);

        assertTrue(result);
        assertEquals("newEncodedPassword", userEntity.getPassword());
        verify(usersRepository).save(userEntity);
    }

    @Test
    void setPassword_shouldReturnFalse_whenCurrentPasswordInvalid() {
        when(usersRepository.findByEmail("test@example.com")).thenReturn(userEntity);
        when(encoder.matches("oldPassword", "encodedPassword")).thenReturn(false);

        boolean result = usersService.setPassword("test@example.com", newPassword);

        assertFalse(result);
        verify(usersRepository, never()).save(userEntity);
    }

    @Test
    void setPassword_shouldReturnFalse_whenUserNotFound() {
        when(usersRepository.findByEmail("unknown@example.com")).thenReturn(null);

        boolean result = usersService.setPassword("unknown@example.com", newPassword);

        assertFalse(result);
    }

    @Test
    void getUser_shouldReturnUserDto_whenUserExists() {
        User expectedDto = new User();
        when(usersRepository.findByEmail("test@example.com")).thenReturn(userEntity);
        when(mapper.toDto(userEntity)).thenReturn(expectedDto);

        User result = usersService.getUser("test@example.com");


        assertEquals(expectedDto, result);
        verify(mapper).toDto(userEntity);
    }

    @Test
    void updateUser_shouldUpdateFieldsAndSave_whenUserExists() {
        when(usersRepository.findByEmail("test@example.com")).thenReturn(userEntity);

        UpdateUser result = usersService.updateUser("test@example.com", updateUser);

        assertEquals("Jane", userEntity.getFirstName());
        assertEquals("Smith", userEntity.getLastName());
        assertEquals("+79997654321", userEntity.getPhone());
        verify(usersRepository).save(userEntity);
        assertEquals(updateUser, result);
    }

    @Test
    void updateUserImage_shouldUpdateImageAndSave_whenFileProvided() throws IOException {
        when(usersRepository.findByEmail("test@example.com")).thenReturn(userEntity);

        ReflectionTestUtils.setField(
                usersService,
                "avatarDirectoryPath",  // имя поля
                "tmp/images"     // тестовое значение
        );

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            mockedFiles.when(() -> Files.createDirectories(any()))
                    .thenAnswer(invocation -> null);
            mockedFiles.when(() -> Files.copy((Path)any(), any(), any()))
                    .thenAnswer(invocation -> null);
            mockedFiles.when(() -> Files.deleteIfExists(any()))
                    .thenAnswer(invocation -> null);

            usersService.updateUserImage("test@example.com", mockFile);

            assertTrue(userEntity.getImage().startsWith("/images/"));
            assertTrue(userEntity.getImage().contains(".png"));

            verify(usersRepository).save(userEntity);
        }
    }
}
