package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;

import java.io.IOException;

/**
 * Сервисный интерфейс для управления пользовательскими данными в системе.
 *
 * <p>Определяет контракт бизнес‑логики работы с пользователями, включая:
 * <ul>
 *   <li>создание новых учётных записей;</li>
 *   <li>смену пароля;</li>
 *   <li>получение информации о пользователе;</li>
 *   <li>редактирование профиля;</li>
 *   <li>обновление аватара.</li>
 * </ul>
 *
 * <p>Реализация этого интерфейса должна обеспечивать:
 * <ul>
 *   <li>валидацию входных данных;</li>
 *   <li>безопасное хранение паролей (с использованием хеширования);</li>
 *   <li>работу с файловой системой/хранилищем при загрузке изображений;</li>
 *   <li>интеграцию с репозиторием для сохранения/извлечения данных.</li>
 * </ul>
 *
 * @see UserEntity
 * @see NewPassword
 * @see UpdateUser
 * @see User
 * @see org.springframework.web.multipart.MultipartFile
 */
public interface UsersService {

    /**
     * Создаёт новую учётную запись пользователя в системе.
     *
     * <p>Алгоритм работы:
     * <ul>
     *   <li>принимает готовую сущность {@link UserEntity} с предварительно валидированными данными;</li>
     *   <li>сохраняет запись в хранилище через репозиторий;</li>
     *   <li>не возвращает результат (операция считается успешной при отсутствии исключений).</li>
     * </ul>
     *
     * @param user сущность {@link UserEntity} с данными нового пользователя
     *         (должна содержать валидные email, пароль, имя и другие обязательные поля)
     * @throws RuntimeException если возникла ошибка при сохранении (например, дубликат email)
     * @see UserEntity
     */
    void createUser(UserEntity user);

    /**
     * Изменяет пароль пользователя.
     *
     * <p>Алгоритм работы:
     * <ul>
     *   <li>проверяет, что текущий пароль (из {@code passwords.currentPassword}) соответствует сохранённому;</li>
     *   <li>если проверка пройдена — хеширует и сохраняет новый пароль ({@code passwords.newPassword});</li>
     *   <li>возвращает {@code true} при успешной смене, {@code false} — если текущий пароль неверен.</li>
     * </ul>
     *
     * @param username имя пользователя (логин/email), для которого меняется пароль
     * @param passwords DTO {@link NewPassword} с текущим и новым паролем
     * @return {@code true}, если пароль успешно изменён; {@code false}, если текущий пароль неверен
     * @see NewPassword
     */
    boolean setPassword(String username, NewPassword passwords);

    /**
     * Получает информацию о пользователе по его имени (логину).
     *
     * <p>Алгоритм работы:
     * <ul>
     *   <li>ищет пользователя в хранилище по полю {@code username};</li>
     *   <li>преобразует найденную сущность {@link UserEntity} в DTO {@link User};</li>
     *   <li>возвращает DTO с публичными данными пользователя.</li>
     * </ul>
     *
     * @param username имя пользователя (логин/email)
     * @return экземпляр {@link User} с информацией о пользователе
     *         или {@code null}, если пользователь не найден
     * @see User
     * @see UserEntity
     */
    User getUser(String username);

    /**
     * Обновляет персональные данные пользователя.
     *
     * <p>Алгоритм работы:
     * <ul>
     *   <li>находит пользователя в хранилище по имени {@code name};</li>
     *   <li>копирует поля из DTO {@link UpdateUser} в сущность;</li>
     *   <li>сохраняет изменения в хранилище;</li>
     *   <li>возвращает обновлённый DTO {@link UpdateUser}.</li>
     * </ul>
     *
     * @param name имя пользователя (логин/email), чьи данные обновляются
     * @param updateUser DTO {@link UpdateUser} с новыми значениями полей
     * @return экземпляр {@link UpdateUser} с актуальными данными после обновления
     * @see UpdateUser
     */
    UpdateUser updateUser(String name, UpdateUser updateUser);

    /**
     * Загружает и сохраняет новое изображение профиля пользователя.
     *
     * <p>Алгоритм работы:
     * <ul>
     *   <li>принимает файл изображения в формате {@link MultipartFile};</li>
     *   <li>сохраняет файл в файловую систему/облачное хранилище;</li>
     *   <li>обновляет поле {@code image} в сущности пользователя;</li>
     *   <li>сохраняет изменения в хранилище.</li>
     * </ul>
     *
     * @param name имя пользователя (логин/email), для которого обновляется аватар
     * @param image файл изображения в формате {@link MultipartFile}
     * @throws IOException если возникла ошибка при чтении/записи файла
     * @throws IllegalArgumentException если файл имеет недопустимый формат или размер
     * @see MultipartFile
     */
    void updateUserImage(String name, MultipartFile image) throws IOException;
}
