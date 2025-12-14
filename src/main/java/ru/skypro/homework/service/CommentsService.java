package ru.skypro.homework.service;

import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;

/**
 * Сервис для бизнес‑логики работы с комментариями к объявлениям.
 *
 * <p>Определяет контракт для основных операций над комментариями:
 * <ul>
 *   <li>получение списка комментариев объявления;</li>
 *   <li>добавление нового комментария;</li>
 *   <li>удаление существующего комментария;</li>
 *   <li>обновление текста комментария.</li>
 * </ul>
 *
 * <p>Каждый метод описывает семантику операции и возможные сценарии использования,
 * оставляя реализацию конкретным классам‑реализаторам.
 *
 * <p>Основные принципы:
 * <ul>
 *   <li>разделение ответственности (бизнес‑логика вынесена в сервис);</li>
 *   <li>инкапсуляция правил доступа (проверка прав пользователя);</li>
 *   <li>использование DTO для передачи данных между слоями.</li>
 * </ul>
 *
 * @see Comment
 * @see Comments
 * @see CreateOrUpdateComment
 */
public interface CommentsService {

    /**
     * Получает список комментариев для указанного объявления.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Находит объявление по идентификатору.</li>
     *   <li>Загружает все комментарии, связанные с этим объявлением.</li>
     *   <li>Преобразует сущности в DTO {@link Comment}.</li>
     *   <li>Формирует объект {@link Comments} с общим количеством и списком комментариев.</li>
     * </ol>
     *
     * @param id идентификатор объявления
     * @return объект {@link Comments}, содержащий:
     *         <ul>
     *           <li>{@code count} — общее количество комментариев;</li>
     *           <li>{@code results} — список объектов {@link Comment}.</li>
     *         </ul>
     *         Если объявление не найдено или комментариев нет, возвращает {@code null}.
     * @see Comments
     * @see Comment
     */
    Comments getComments(int id);

    /**
     * Добавляет новый комментарий к указанному объявлению.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Проверяет существование объявления по идентификатору.</li>
     *   <li>Создаёт новую сущность комментария на основе данных из {@link CreateOrUpdateComment}.</li>
     *   <li>Устанавливает связь с автором (по имени/email).</li>
     *   <li>Сохраняет комментарий в хранилище.</li>
     *   <li>Возвращает DTO {@link Comment} с заполненными данными.</li>
     * </ol>
     *
     * @param name имя (email) пользователя, добавляющего комментарий
     * @param id идентификатор объявления, к которому добавляется комментарий
     * @param properties DTO {@link CreateOrUpdateComment} с текстом комментария
     * @return объект {@link Comment} с данными нового комментария или {@code null},
     *         если объявление не найдено
     * @see CreateOrUpdateComment
     * @see Comment
     */
    Comment addComment(String name, int id, CreateOrUpdateComment properties);

    /**
     * Удаляет комментарий к объявлению с проверкой прав доступа.
     *
     * <p>Возвращает код результата:
     * <ul>
     *   <li>0 — успешно удалено;</li>
     *   <li>1 — объявление или комментарий не найдены;</li>
     *   <li>2 — у пользователя нет прав на удаление (не автор комментария).</li>
     * </ul>
     *
     * @param name имя (email) пользователя, пытающегося удалить комментарий
     * @param adId идентификатор объявления
     * @param commentId идентификатор комментария
     * @return код результата операции (0, 1 или 2)
     */
    int deleteComment(String name, int adId, int commentId);

    /**
     * Обновляет текст существующего комментария с проверкой прав доступа.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Находит комментарий по идентификатору.</li>
     *   <li>Проверяет, что пользователь является автором комментария.</li>
     *   <li>Обновляет текст комментария на значение из {@link CreateOrUpdateComment}.</li>
     *   <li>Сохраняет изменения в хранилище.</li>
     *   <li>Возвращает обновлённый DTO {@link Comment}.</li>
     * </ol>
     *
     * @param name имя (email) пользователя, пытающегося обновить комментарий
     * @param properties DTO {@link CreateOrUpdateComment} с новым текстом комментария
     * @param commentId идентификатор комментария
     * @param adId идентификатор объявления
     * @return объект {@link Comment} с обновлёнными данными или {@code null},
     *         если комментарий или объявление не найдены
     * @throws SecurityException если у пользователя нет прав на обновление
     * @see CreateOrUpdateComment
     * @see Comment
     */
    Comment updateComment(String name, CreateOrUpdateComment properties, int commentId, int adId);
}
