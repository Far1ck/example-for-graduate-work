package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.UpdateUser;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import ru.skypro.homework.dto.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.service.UsersService;

import java.io.IOException;

/**
 * Контроллер для управления данными пользователей в системе.
 *
 * <p>Предоставляет REST‑конечные точки для:
 * <ul>
 *   <li>обновления аватара пользователя;</li>
 *   <li>редактирования персональных данных профиля;</li>
 *   <li>получения информации о текущем пользователе;</li>
 *   <li>смены пароля учётной записи.</li>
 * </ul>
 *
 * <p>Основные особенности:
 * <ul>
 *   <li>помечен аннотацией {@link RestController}, что автоматически делает все методы возвращающими данные в формате JSON;</li>
 *   <li>поддерживает CORS‑запросы с источника {@code http://localhost:3000};</li>
 *   <li>все операции требуют аутентификации (объект {@link Authentication} передаётся в методах);</li>
 *   <li>использует Swagger/OpenAPI для документирования API ({@link Operation}, {@link ApiResponse});</li>
 *   <li>зависит от сервиса {@link UsersService} для бизнес‑логики работы с пользователями.</li>
 * </ul>
 *
 * @see CrossOrigin
 * @see RestController
 * @see RequestMapping
 * @see RequiredArgsConstructor
 * @see UsersService
 * @see Authentication
 */
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    /**
     * Сервис для работы с пользовательскими данными.
     *
     * <p>Отвечает за:
     * <ul>
     *   <li>обновление аватара;</li>
     *   <li>редактирование профиля;</li>
     *   <li>получение информации о пользователе;</li>
     *   <li>смену пароля.</li>
     * </ul>
     *
     * <p>Внедряется через конструктор благодаря аннотации {@link RequiredArgsConstructor}.
     *
     * @see UsersService
     */
    private final UsersService usersService;

    /**
     * Обновляет аватар авторизованного пользователя.
     *
     * <p>Endpoint: {@code PATCH /users/me/image}
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Принимает файл изображения через параметр {@code image}.</li>
     *   <li>Проверяет, что файл не пустой и имеет MIME‑тип, начинающийся с {@code image/}.</li>
     *   <li>Передаёт данные в сервис {@link UsersService#updateUserImage} для обработки.</li>
     *   <li>Возвращает соответствующий HTTP‑статус.</li>
     * </ol>
     *
     * @param image загружаемое изображение в формате multipart/form-data
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link ResponseEntity} со статусом:
     *         <ul>
     *           <li>{@code 200 OK} при успешном обновлении;</li>
     *           <li>{@code 400 Bad Request} если файл пуст или имеет неверный тип;</li>
     *           <li>{@code 401 Unauthorized} если пользователь не аутентифицирован.</li>
     *         </ul>
     * @throws IOException если возникла ошибка при работе с файлом
     * @see MultipartFile
     * @see UsersService#updateUserImage(String, MultipartFile)
     */
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Обновление аватара авторизованного пользователя",
            tags = {"Пользователи"},
            operationId = "updateUserImage"
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<Void> updateUserImage(@RequestParam("image") MultipartFile image, Authentication authentication) throws IOException {
        if (image.isEmpty() || !image.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().build();
        }
        usersService.updateUserImage(authentication.getName(), image);
        return ResponseEntity.ok().build();
    }

    /**
     * Обновляет информацию о профиле авторизованного пользователя.
     *
     * <p>Endpoint: {@code PATCH /users/me}
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Принимает объект {@link UpdateUser} с новыми данными профиля.</li>
     *   <li>Передаёт данные в сервис {@link UsersService#updateUser} для обработки.</li>
     *   <li>Возвращает обновлённый объект {@link UpdateUser} в теле ответа.</li>
     * </ol>
     *
     * @param updateUser DTO с новыми данными пользователя ({@link UpdateUser})
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link ResponseEntity} с обновлённым объектом {@link UpdateUser} и статусом {@code 200 OK},
     *         или {@code 401 Unauthorized} если пользователь не аутентифицирован
     * @see UpdateUser
     * @see UsersService#updateUser(String, UpdateUser)
     */
    @PatchMapping("/me")
    @Operation(
            summary = "Обновление информации об авторизованном пользователе",
            tags = {"Пользователи"},
            operationId = "updateUser"
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<UpdateUser> updateUser(@RequestBody UpdateUser updateUser, Authentication authentication) {
        return ResponseEntity.ok(usersService.updateUser(authentication.getName(), updateUser));
    }

    /**
     * Получает информацию о текущем авторизованном пользователе.
     *
     * <p>Endpoint: {@code GET /users/me}
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Извлекает имя пользователя из объекта {@link Authentication}.</li>
     *   <li>Обращается к сервису {@link UsersService#getUser} для получения данных.</li>
     *   <li>Возвращает объект {@link User} в теле ответа.</li>
     * </ol>
     *
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link ResponseEntity} с объектом {@link User} и статусом {@code 200 OK},
     *         или {@code 401 Unauthorized} если пользователь не аутентифицирован
     * @see User
     * @see UsersService#getUser(String)
     */
    @GetMapping("/me")
    @Operation(
            summary = "Получение информации об авторизованном пользователе",
            tags = {"Пользователи"},
            operationId = "getUser"
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<User> getUser(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(usersService.getUser(username));
    }

    /**
     * Обновляет пароль авторизованного пользователя.
     *
     * <p>Endpoint: {@code POST /users/set_password}
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Принимает объект {@link NewPassword} с текущим и новым паролем.</li>
     *   <li>Передаёт данные в сервис {@link UsersService#setPassword} для проверки и обновления.</li>
     *   <li>Если пароль успешно изменён — возвращает статус {@code 200 OK}.</li>
     *   <li>Если операция отклонена (например, неверный текущий пароль) — возвращает статус {@code 403 Forbidden}.</li>
     * </ol>
     *
     * @param newPassword DTO с текущим и новым паролем ({@link NewPassword})
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link ResponseEntity} со статусом:
     *         <ul>
     *           <li>{@code 200 OK} при успешной смене пароля;</li>
     *           <li>{@code 403 Forbidden} если смена пароля отклонена (например, не совпал текущий пароль);</li>
     *           <li>{@code 401 Unauthorized} если пользователь не аутентифицирован.</li>
     *         </ul>
     * @see NewPassword
     * @see UsersService#setPassword(String, NewPassword)
     */
    @PostMapping("/set_password")
    @Operation(
            summary = "Обновление пароля",
            tags = {"Пользователи"},
            operationId = "setPassword"
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    public ResponseEntity<Void> setPassword(@RequestBody NewPassword newPassword, Authentication authentication) {
        if (usersService.setPassword(authentication.getName(), newPassword)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
