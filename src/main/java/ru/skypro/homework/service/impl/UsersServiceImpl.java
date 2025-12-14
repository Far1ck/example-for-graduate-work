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
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.Random;

/**
 * Реализация сервиса {@link UsersService} для управления пользовательскими данными.
 *
 * <p>Обеспечивает бизнес‑логику операций с пользователями, включая:
 * <ul>
 *   <li>создание учётных записей;</li>
 *   <li>смену пароля с хешированием;</li>
 *   <li>получение информации о пользователе;</li>
 *   <li>редактирование профиля;</li>
 *   <li>загрузку и обновление аватара с сохранением файлов на диске.</li>
 * </ul>
 *
 * <p>Основные зависимости:
 * <ul>
 *   <li>{@link UsersRepository} — для доступа к данным пользователей;</li>
 *   <li>{@link PasswordEncoder} — для безопасного хеширования паролей;</li>
 *   <li>{@link UserMapper} — для преобразования между сущностями и DTO.</li>
 * </ul>
 *
 * <p>Конфигурируется через:
 * <ul>
 *   <li>внедрение зависимостей в конструктор;</li>
 *   <li>свойство {@code app.images.dir} из конфигурации (путь к директории аватаров).</li>
 * </ul>
 *
 * @see Service
 * @see UsersService
 * @see UsersRepository
 * @see PasswordEncoder
 * @see UserMapper
 */
@Service
public class UsersServiceImpl implements UsersService {

    /**
     * Репозиторий для доступа к данным пользователей в базе данных.
     *
     * @see UsersRepository
     */
    private final UsersRepository usersRepository;

    /**
     * Компонент для хеширования и проверки паролей.
     *
     * <p>Используется для:
     * <ul>
     *   <li>проверки текущего пароля при смене;</li>
     *   <li>хеширования нового пароля перед сохранением.</li>
     * </ul>
     *
     * @see PasswordEncoder
     */
    private final PasswordEncoder encoder;

    /**
     * Маппер для преобразования между {@link UserEntity} и DTO ({@link User}, {@link UpdateUser}).
     *
     * @see UserMapper
     */
    private final UserMapper mapper;

    /**
     * Генератор случайных чисел для создания уникальных имён файлов аватаров.
     */
    private final Random rnd = new Random();

    /**
     * Путь к директории для хранения аватаров пользователей.
     *
     * <p>Задаётся через свойство конфигурации {@code app.images.dir}.
     * Пример значения: {@code /var/images/avatars}.
     */
    @Value("${app.images.dir}")
    private String avatarDirectoryPath;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param usersRepository репозиторий пользователей
     * @param encoder компонент для хеширования паролей
     * @param mapper маппер для преобразования сущностей и DTO
     */
    public UsersServiceImpl(UsersRepository usersRepository, PasswordEncoder encoder, UserMapper mapper) {
        this.usersRepository = usersRepository;
        this.encoder = encoder;
        this.mapper = mapper;
    }

    /**
     * Создаёт новую учётную запись пользователя.
     *
     * <p>Сохраняет переданную сущность {@link UserEntity} в базу данных через репозиторий.
     *
     * @param user сущность {@link UserEntity} с данными нового пользователя
     * @throws RuntimeException если возникла ошибка при сохранении (например, дубликат email)
     * @see UsersRepository#save(Object)  
     */
    @Override
    public void createUser(UserEntity user) {
        usersRepository.save(user);
    };

    /**
     * Изменяет пароль пользователя.
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Находит пользователя по email.</li>
     *   <li>Проверяет текущий пароль через {@link PasswordEncoder#matches}.</li>
     *   <li>Если пароль верен — хеширует новый пароль и сохраняет его.</li>
     *   <li>Возвращает результат операции.</li>
     * </ol>
     *
     * @param username email пользователя (используется как логин)
     * @param password DTO {@link NewPassword} с текущим и новым паролем
     * @return {@code true}, если пароль успешно изменён; {@code false}, если текущий пароль неверен
     * @see PasswordEncoder#matches(CharSequence, String)
     * @see PasswordEncoder#encode(CharSequence)
     */
    @Override
    public boolean setPassword(String username, NewPassword password) {
        UserEntity user = usersRepository.findByEmail(username);
        if (!encoder.matches(password.getCurrentPassword(), user.getPassword())) {
            return false;
        }
        user.setPassword(encoder.encode(password.getNewPassword()));
        usersRepository.save(user);
        return true;
    }

    /**
     * Получает информацию о пользователе по email.
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Находит сущность {@link UserEntity} по email через репозиторий.</li>
     *   <li>Преобразует её в DTO {@link User} с помощью {@link UserMapper#toDto}.</li>
     *   <li>Возвращает DTO.</li>
     * </ol>
     *
     * @param username email пользователя (используется как логин)
     * @return экземпляр {@link User} или {@code null}, если пользователь не найден
     * @see UsersRepository#findByEmail(String)
     * @see UserMapper#toDto(UserEntity)
     */
    @Override
    public User getUser(String username) {
        UserEntity user = usersRepository.findByEmail(username);
        return mapper.toDto(user);
    }

    /**
     * Обновляет персональные данные пользователя.
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Находит пользователя по email.</li>
     *   <li>Копирует поля из DTO {@link UpdateUser} в сущность.</li>
     *   <li>Сохраняет изменения в базе данных.</li>
     *   <li>Возвращает переданный DTO (можно модифицировать для возврата обновлённого состояния).</li>
     * </ol>
     *
     * @param username email пользователя (используется как логин)
     * @param updateUser DTO {@link UpdateUser} с новыми данными
     * @return экземпляр {@link UpdateUser}, переданный на вход
     * @see UsersRepository#findByEmail(String)
     * @see UsersRepository#save(Object) 
     */
    @Override
    public UpdateUser updateUser(String username, UpdateUser updateUser) {
        UserEntity user = usersRepository.findByEmail(username);
        user.setFirstName(updateUser.getFirstName());
        user.setLastName(updateUser.getLastName());
        user.setPhone(updateUser.getPhone());
        usersRepository.save(user);
        return updateUser;
    }

    /**
    * Загружает и сохраняет новое изображение профиля пользователя.
    *
    * <p>Алгоритм работы:
    * <ol>
    *   <li>Создаёт директорию для аватаров, если её нет.</li>
    *   <li>Находит текущего пользователя по email.</li>
    *   <li>Удаляет старый аватар (если существует).</li>
    *   <li>Генерирует уникальное имя файла для нового аватара.</li>
    *   <li>Копирует файл в целевую директорию.</li>
    *   <li>Обновляет поле {@code image} в сущности пользователя (путь вида {@code /images/имя_файла}).</li>
    *   <li>Сохраняет изменения в базе данных.</li>
    * </ol>
    *
    * @param name email пользователя (используется как логин)
    * @param image файл изображения в формате {@link MultipartFile}
    * @throws IOException если возникла ошибка при работе с файловой системой
    * @throws IllegalArgumentException если файл не имеет расширения или путь некорректен
    * @see Files#createDirectories(Path, FileAttribute[]) 
    * @see Files#deleteIfExists(Path)
    * @see Files#copy(Path, Path, CopyOption...)
    * @see Paths#get(String, String...) 
    */
    @Override
    public void updateUserImage(String name, MultipartFile image) throws IOException {
        Path avatarDirectory = Paths.get(avatarDirectoryPath);
        Files.createDirectories(avatarDirectory);
        UserEntity user = usersRepository.findByEmail(name);
        Path oldFilePath = avatarDirectory.resolve(user.getImage());
        Files.deleteIfExists(oldFilePath);
        String extension = getFileExtension(image.getOriginalFilename());
        String fileName = System.currentTimeMillis() + rnd.nextInt(1000) + extension;
        Path filePath = avatarDirectory.resolve(fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        user.setImage("/images/" + fileName);
        usersRepository.save(user);
    }

    /**
     * Извлекает расширение файла из его оригинального имени.
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Находит позицию последней точки ('.') в имени файла.</li>
     *   <li>Возвращает подстроку от этой позиции до конца (включая точку).</li>
     * </ol>
     *
     * <p>Пример: для имени {@code "avatar.jpg"} вернёт {@code ".jpg"}.
     *
     * @param filename оригинальное имя файла (например, из {@link MultipartFile#getOriginalFilename})
     * @return расширение файла в виде строки (с точкой), например {@code ".png"}
     * @throws IllegalArgumentException если имя файла не содержит точки или пусто
     */
    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.'));
    }
}
