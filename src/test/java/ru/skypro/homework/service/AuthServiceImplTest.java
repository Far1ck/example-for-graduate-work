package ru.skypro.homework.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.service.impl.AuthServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserDetailsManager manager;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UsersService usersService;

    @InjectMocks
    private AuthServiceImpl authService;

    private Register registerDto;
    private UserEntity userEntity;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        registerDto = new Register();
        registerDto.setUsername("test@example.com");
        registerDto.setFirstName("John");
        registerDto.setLastName("Doe");
        registerDto.setPhone("+79991234567");
        registerDto.setRole(Role.USER);
        registerDto.setPassword("password123");

        userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");
        userEntity.setPhone("+79991234567");
        userEntity.setRole("USER");
        userEntity.setPassword("encodedPassword");
        userEntity.setEnabled(true);

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("encodedPassword")
                .roles("USER")
                .build();
    }

    // --- Тесты метода login ---

    @Test
    void login_shouldReturnTrue_whenCredentialsValid() {
        // Мокируем существование пользователя и совпадение пароля
        when(manager.userExists("test@example.com")).thenReturn(true);
        when(manager.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(encoder.matches("password123", "encodedPassword")).thenReturn(true);

        boolean result = authService.login("test@example.com", "password123");

        assertTrue(result);
        verify(manager).loadUserByUsername("test@example.com");
        verify(encoder).matches("password123", "encodedPassword");
    }

    @Test
    void login_shouldReturnFalse_whenUserDoesNotExist() {
        when(manager.userExists("unknown@example.com")).thenReturn(false);

        boolean result = authService.login("unknown@example.com", "anyPassword");

        assertFalse(result);
        // loadUserByUsername и matches не должны вызываться
        verify(manager, never()).loadUserByUsername(any());
        verify(encoder, never()).matches(any(), any());
    }

    @Test
    void login_shouldReturnFalse_whenPasswordMismatch() {
        when(manager.userExists("test@example.com")).thenReturn(true);
        when(manager.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(encoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        boolean result = authService.login("test@example.com", "wrongPassword");

        assertFalse(result);
        verify(encoder).matches("wrongPassword", "encodedPassword");
    }

    // --- Тесты метода register ---

    @Test
    void register_shouldReturnTrue_whenUsernameUniqueAndSaveSuccess() {
        // Пользователь не существует
        when(manager.userExists("test@example.com")).thenReturn(false);
        // Кодирование пароля
        when(encoder.encode("password123")).thenReturn("encodedPassword");
        // Сохранение проходит успешно
        doNothing().when(usersService).createUser(userEntity);


        boolean result = authService.register(registerDto);

        assertTrue(result);
        verify(manager).userExists("test@example.com");
        verify(encoder).encode("password123");
        verify(usersService).createUser(userEntity);
    }

    @Test
    void register_shouldReturnFalse_whenUsernameAlreadyExists() {
        when(manager.userExists("test@example.com")).thenReturn(true);

        boolean result = authService.register(registerDto);

        assertFalse(result);
        // Остальные шаги не должны выполняться
        verify(encoder, never()).encode(any());
        verify(usersService, never()).createUser(any());
    }

    @Test
    void register_shouldReturnFalse_whenCreateUserThrowsException() {
        when(manager.userExists("test@example.com")).thenReturn(false);
        when(encoder.encode("password123")).thenReturn("encodedPassword");
        // Имитируем ошибку сохранения
        doThrow(new RuntimeException("DB error")).when(usersService).createUser(userEntity);

        assertThrows(RuntimeException.class, () -> authService.register(registerDto));
        verify(usersService).createUser(userEntity);
    }

    @Test
    void register_shouldMapAllFieldsCorrectly() {
        when(manager.userExists("test@example.com")).thenReturn(false);
        when(encoder.encode("password123")).thenReturn("encodedPassword");
        doNothing().when(usersService).createUser(any(UserEntity.class));

        authService.register(registerDto);

        // Проверяем, что все поля DTO корректно скопированы в UserEntity
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(usersService).createUser(captor.capture());

        UserEntity savedUser = captor.getValue();
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("John", savedUser.getFirstName());
        assertEquals("Doe", savedUser.getLastName());
        assertEquals("+79991234567", savedUser.getPhone());
        assertEquals("USER", savedUser.getRole());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertTrue(savedUser.isEnabled());
    }
}
