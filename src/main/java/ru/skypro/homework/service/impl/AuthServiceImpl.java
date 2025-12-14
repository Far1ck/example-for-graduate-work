package ru.skypro.homework.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.service.UsersService;

/**
 * Реализация сервиса аутентификации и регистрации пользователей с использованием Spring Security.
 *
 * <p>Обеспечивает:
 * <ul>
 *   <li>проверку подлинности пользователей через {@link UserDetailsManager};</li>
 *   <li>регистрацию новых пользователей с хешированием паролей;</li>
 *   <li>интеграцию с сервисным слоем для работы с пользовательскими данными.</li>
 * </ul>
 *
 * <p>Основные зависимости:
 * <ul>
 *   <li>{@link UserDetailsManager} — для управления учётными записями Spring Security;</li>
 *   <li>{@link PasswordEncoder} — для безопасного хеширования паролей;</li>
 *   <li>{@link UsersService} — для сохранения пользовательских данных в хранилище.</li>
 * </ul>
 *
 * @see AuthService
 * @see Service
 */
@Service
public class AuthServiceImpl implements AuthService {

    /**
     * Менеджер учётных записей Spring Security для операций с пользователями.
     *
     * <p>Используется для:
     * <ul>
     *   <li>проверки существования пользователя;</li>
     *   <li>получения данных пользователя по имени;</li>
     *   <li>управления жизненным циклом учётных записей.</li>
     * </ul>
     *
     * @see UserDetailsManager
     */
    private final UserDetailsManager manager;

    /**
     * Кодировщик паролей для безопасного хранения учётных данных.
     *
     * <p>Применяет криптографическое хеширование (например, BCrypt) для:
     * <ul>
     *   <li>кодирования паролей при регистрации;</li>
     *   <li>сравнения хешей при аутентификации.</li>
     * </ul>
     *
     * @see PasswordEncoder
     */
    private final PasswordEncoder encoder;

    /**
     * Сервис для работы с пользовательскими сущностями в хранилище данных.
     *
     * <p>Отвечает за:
     * <ul>
     *   <li>создание и сохранение записей пользователей;</li>
     *   <li>взаимодействие с репозиторием/DAO.</li>
     * </ul>
     *
     * @see UsersService
     */
    private final UsersService usersService;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param manager менеджер учётных записей Spring Security
     * @param passwordEncoder кодировщик паролей
     * @param usersService сервис для работы с пользовательскими данными
     */
    public AuthServiceImpl(UserDetailsManager manager,
                           PasswordEncoder passwordEncoder,
                           UsersService usersService) {
        this.manager = manager;
        this.encoder = passwordEncoder;
        this.usersService = usersService;
    }

    /**
     * Выполняет аутентификацию пользователя по имени и паролю.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Проверяет существование пользователя в системе.</li>
     *   <li>Загружает данные пользователя через {@link UserDetailsManager#loadUserByUsername}.</li>
     *   <li>Сравнивает предоставленный пароль с хешем из хранилища через {@link PasswordEncoder#matches}.</li>
     * </ol>
     *
     * @param userName имя пользователя (логин/email)
     * @param password пароль в исходном виде
     * @return {@code true} при успешной аутентификации; {@code false} если:
     *         <ul>
     *           <li>пользователь не найден;</li>
     *           <li>пароль не совпадает с сохранённым хешем.</li>
     *         </ul>
     * @see UserDetailsManager#userExists(String)
     * @see UserDetailsManager#loadUserByUsername(String)
     * @see PasswordEncoder#matches(CharSequence, String)
     */
    @Override
    public boolean login(String userName, String password) {
        if (!manager.userExists(userName)) {
            return false;
        }
        UserDetails userDetails = manager.loadUserByUsername(userName);
        return encoder.matches(password, userDetails.getPassword());
    }

    /**
     * Регистрирует нового пользователя в системе.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Проверяет уникальность имени пользователя.</li>
     *   <li>Создаёт сущность {@link UserEntity} на основе данных из {@link Register}.</li>
     *   <li>Хеширует пароль с помощью {@link PasswordEncoder}.</li>
     *   <li>Сохраняет запись через {@link UsersService#createUser}.</li>
     * </ol>
     *
     * @param register DTO с данными для регистрации (имя, пароль, контакты, роль)
     * @return {@code true} при успешной регистрации; {@code false} если:
     *         <ul>
     *           <li>имя пользователя уже занято;</li>
     *           <li>возникла ошибка при сохранении данных.</li>
     *         </ul>
     * @see Register
     * @see UserEntity
     * @see UsersService#createUser(UserEntity)
     */
    @Override
    public boolean register(Register register) {
        if (manager.userExists(register.getUsername())) {
            return false;
        }
        UserEntity newUser = new UserEntity();
        newUser.setEmail(register.getUsername());
        newUser.setFirstName(register.getFirstName());
        newUser.setLastName(register.getLastName());
        newUser.setPhone(register.getPhone());
        newUser.setRole(register.getRole().name());
        newUser.setPassword(encoder.encode(register.getPassword()));
        newUser.setEnabled(true);
        usersService.createUser(newUser);
        return true;
    }

}
