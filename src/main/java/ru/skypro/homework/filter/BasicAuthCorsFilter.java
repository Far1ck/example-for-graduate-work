package ru.skypro.homework.filter;


import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Фильтр CORS для установки заголовка {@code Access-Control-Allow-Credentials} в HTTP‑ответах.
 *
 * <p>Данный фильтр расширяет {@link OncePerRequestFilter}, гарантируя однократное выполнение
 * для каждого входящего HTTP‑запроса. Основная задача — добавление заголовка, разрешающего
 * использование учётных данных (cookies, авторизации) в кросс‑доменных запросах.
 *
 * <p>Фильтр добавляет заголовок:
 * <pre>
 * Access-Control-Allow-Credentials: true
 * </pre>
 *
 * что позволяет клиентским приложениям отправлять учётные данные при кросс‑доменных вызовах.
 *
 * @see Component
 * @see OncePerRequestFilter
 * @see HttpServletRequest
 * @see HttpServletResponse
 * @see FilterChain
 */
@Component
public class BasicAuthCorsFilter extends OncePerRequestFilter {

    /**
     * Выполняет фильтрацию HTTP‑запроса, добавляя необходимый CORS‑заголовок в ответ.
     *
     * <p>Метод:
     * <ol>
     *   <li>Добавляет в HTTP‑ответ заголовок {@code Access-Control-Allow-Credentials} со значением {@code true},
     *       что разрешает использование учётных данных в кросс‑доменных запросах.</li>
     *   <li>Передаёт запрос и ответ далее по цепочке фильтров ({@link FilterChain#doFilter}).</li>
     * </ol>
     *
     * <p><b>Важно:</b> данный фильтр не модифицирует запрос и не выполняет аутентификацию —
     * он только устанавливает CORS‑заголовок для корректной работы кросс‑доменных запросов
     * с учётными данными.
     *
     * @param httpServletRequest  входящий HTTP‑запрос
     * @param httpServletResponse исходящий HTTP‑ответ, в который добавляется заголовок
     * @param filterChain        цепочка фильтров, через которую передаётся запрос/ответ
     * @throws ServletException если возникает ошибка на уровне сервлета
     * @throws IOException        если возникает ошибка ввода‑вывода при обработке запроса/ответа
     * @see HttpServletRequest
     * @see HttpServletResponse
     * @see FilterChain#doFilter( ServletRequest , ServletResponse )
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
