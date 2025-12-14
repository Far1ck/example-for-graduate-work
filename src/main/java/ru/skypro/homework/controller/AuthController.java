package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.homework.dto.Login;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.service.AuthService;

/**
 * Контроллер для обработки аутентификационных операций: входа в систему и регистрации пользователей.
 *
 * <p>Предоставляет REST‑конечные точки для:
 * <ul>
 *   <li>аутентификации пользователя по логину и паролю ({@code /login});</li>
 *   <li>регистрации нового пользователя в системе ({@code /register}).</li>
 * </ul>
 *
 * <p>Основные особенности:
 * <ul>
 *   <li>помечен аннотацией {@link RestController}, что автоматически делает все методы возвращающими
 *       данные в формате JSON;</li>
 *   <li>поддерживает CORS‑запросы с источника {@code http://localhost:3000};</li>
 *   <li>использует логирование через SLF4J ({@link Slf4j});</li>
 *   <li>зависит от сервиса {@link AuthService} для бизнес‑логики аутентификации и регистрации.</li>
 * </ul>
 *
 * @see Slf4j
 * @see CrossOrigin
 * @see RestController
 * @see RequiredArgsConstructor
 * @see AuthService
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class AuthController {

    /**
     * Сервис аутентификации и регистрации пользователей.
     *
     * <p>Отвечает за:
     * <ul>
     *   <li>проверку подлинности учётных данных при входе;</li>
     *   <li>создание новых учётных записей;</li>
     *   <li>валидацию данных регистрации.</li>
     * </ul>
     *
     * <p>Внедряется через конструктор благодаря аннотации {@link RequiredArgsConstructor}.
     *
     * @see AuthService
     */
    private final AuthService authService;

    /**
     * Обрабатывает HTTP POST‑запрос на аутентификацию пользователя.
     *
     * <p>Endpoint: {@code POST /login}
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Принимает объект {@link Login} с полями {@code username} и {@code password} из тела запроса.</li>
     *   <li>Передаёт данные в сервис {@link AuthService#login(String, String)} для проверки.</li>
     *   <li>Возвращает:
     *     <ul>
     *       <li>{@code 200 OK}, если аутентификация прошла успешно;</li>
     *       <li>{@code 401 UNAUTHORIZED}, если логин или пароль неверны.</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param login объект DTO с учётными данными пользователя ({@link Login})
     * @return {@link ResponseEntity} с соответствующим HTTP‑статусом
     * @see Login
     * @see AuthService#login(String, String)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login) {
        if (authService.login(login.getUsername(), login.getPassword())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Обрабатывает HTTP POST‑запрос на регистрацию нового пользователя.
     *
     * <p>Endpoint: {@code POST /register}
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Принимает объект {@link Register} с данными нового пользователя из тела запроса.</li>
     *   <li>Передаёт данные в сервис {@link AuthService#register(Register)} для создания учётной записи.</li>
     *   <li>Возвращает:
     *     <ul>
     *       <li>{@code 201 CREATED}, если регистрация прошла успешно;</li>
     *       <li>{@code 400 BAD_REQUEST}, если данные некорректны или регистрация не удалась.</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param register объект DTO с данными для регистрации пользователя ({@link Register})
     * @return {@link ResponseEntity} с соответствующим HTTP‑статусом
     * @see Register
     * @see AuthService#register(Register)
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Register register) {
        if (authService.register(register)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
