package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.service.CommentsService;

/**
 * Контроллер для управления комментариями к объявлениям в API.
 *
 * <p>Предоставляет REST‑конечные точки для:
 * <ul>
 *   <li>получения списка комментариев к объявлению;</li>
 *   <li>добавления нового комментария;</li>
 *   <li>удаления существующего комментария;</li>
 *   <li>обновления текста комментария.</li>
 * </ul>
 *
 * <p>Основные особенности:
 * <ul>
 *   <li>работает по маршруту {@code /ads} (объявления);</li>
 *   <li>использует CORS‑настройки для взаимодействия с фронтендом на {@code http://localhost:3000};</li>
 *   <li>требует аутентификации для всех операций (через параметр {@link Authentication});</li>
 *   <li>возвращает стандартные HTTP‑статусы с описаниями через Swagger‑аннотации.</li>
 * </ul>
 *
 * <p>Класс аннотирован:
 * <ul>
 *   <li>{@link CrossOrigin} — разрешает кросс‑доменные запросы;</li>
 *   <li>{@link RestController} — обозначает как REST‑контроллер Spring;</li>
 *   <li>{@link RequestMapping} — задаёт базовый путь для всех эндпоинтов;</li>
 *   <li>{@link RequiredArgsConstructor} — генерирует конструктор для внедрения зависимостей.</li>
 * </ul>
 *
 * @see CrossOrigin
 * @see RestController
 * @see RequestMapping
 * @see RequiredArgsConstructor
 * @see CommentsService
 */
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class CommentsController {

    /**
     * Сервис для бизнес‑логики работы с комментариями.
     *
     * <p>Внедряется через конструктор (благодаря {@link RequiredArgsConstructor}).
     *
     * @see CommentsService
     */
    private final CommentsService commentsService;

    /**
     * Получает список комментариев для указанного объявления.
     *
     * <p>Endpoint: {@code GET /ads/{id}/comments}
     *
     * @param id идентификатор объявления
     * @return {@link ResponseEntity} с объектом {@link Comments}:
     *         <ul>
     *           <li>{@code 200 OK} — список комментариев успешно получен;</li>
     *           <li>{@code 404 Not Found} — объявление не найдено.</li>
     *         </ul>
     * @see Comments
     * @see CommentsService#getComments(int)
     */
    @GetMapping("/{id}/comments")
    @Operation(
            summary = "Получение комментариев объявления",
            tags = {"Комментарии"},
            operationId = "getComments"
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<Comments> getComments(@PathVariable("id") int id) {
        Comments comments = commentsService.getComments(id);
        if (comments == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(comments);
    }

    /**
     * Добавляет новый комментарий к указанному объявлению.
     *
     * <p>Endpoint: {@code POST /ads/{id}/comments}
     *
     * @param id идентификатор объявления
     * @param properties DTO {@link CreateOrUpdateComment} с текстом комментария
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link ResponseEntity} с объектом {@link Comment}:
     *         <ul>
     *           <li>{@code 200 OK} — комментарий успешно добавлен;</li>
     *           <li>{@code 404 Not Found} — объявление не найдено.</li>
     *         </ul>
     * @see CreateOrUpdateComment
     * @see Comment
     * @see CommentsService#addComment(String, int, CreateOrUpdateComment)
     */
    @PostMapping("/{id}/comments")
    @Operation(
            summary = "Добавление комментария к объявлению",
            tags = {"Комментарии"},
            operationId = "addComment"
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<Comment> addComment(@PathVariable("id") int id,
                                              @RequestBody CreateOrUpdateComment properties,
                                              Authentication authentication) {
        Comment comment = commentsService.addComment(authentication.getName(), id, properties);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(comment);
    }

    /**
     * Удаляет комментарий к объявлению.
     *
     * <p>Endpoint: {@code DELETE /ads/{adId}/comments/{commentId}}
     *
     * @param adId идентификатор объявления
     * @param commentId идентификатор комментария
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link ResponseEntity} без тела:
     *         <ul>
     *           <li>{@code 200 OK} — комментарий успешно удалён;</li>
     *           <li>{@code 404 Not Found} — объявление или комментарий не найдены;</li>
     *           <li>{@code 403 Forbidden} — у пользователя нет прав на удаление.</li>
     *         </ul>
     * @see CommentsService#deleteComment(String, int, int)
     */
    @DeleteMapping("/{adId}/comments/{commentId}")
    @Operation(
            summary = "Удаление комментария",
            tags = {"Комментарии"},
            operationId = "deleteComment"
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<Void> deleteComment(@PathVariable("adId") int adId,
                                           @PathVariable("commentId") int commentId,
                                              Authentication authentication) {
        int result = commentsService.deleteComment(authentication.getName(), adId, commentId);
        if (result == 1) {
            return ResponseEntity.notFound().build();
        }
        if (result == 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Обновляет текст существующего комментария.
     *
     * <p>Endpoint: {@code PATCH /ads/{adId}/comments/{commentId}}
     *
     * @param adId идентификатор объявления
     * @param commentId идентификатор комментария
     * @param properties DTO {@link CreateOrUpdateComment} с новым текстом комментария
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link ResponseEntity} с обновлённым объектом {@link Comment}:
     *         <ul>
     *           <li>{@code 200 OK} — комментарий успешно обновлён;</li>
     *           <li>{@code 404 Not Found} — комментарий или объявление не найдены;</li>
     *           <li>{@code 403 Forbidden} — у пользователя нет прав на обновление.</li>
     *         </ul>
     * @throws SecurityException если пользователь не имеет прав на обновление
     * @see CreateOrUpdateComment
     * @see Comment
     * @see CommentsService#updateComment(String, CreateOrUpdateComment, int, int)
     */
    @PatchMapping("/{adId}/comments/{commentId}")
    @Operation(
            summary = "Обновление комментария",
            tags = {"Комментарии"},
            operationId = "updateComment"
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<Comment> updateComment(@PathVariable("adId") int adId,
                                           @PathVariable("commentId") int commentId,
                                           @RequestBody CreateOrUpdateComment properties,
                                                 Authentication authentication) {
        try {
            Comment comment = commentsService.updateComment(authentication.getName(), properties, commentId, adId);
            if (comment == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(comment);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
