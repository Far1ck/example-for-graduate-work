package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentsRepository;
import ru.skypro.homework.repository.UsersRepository;
import ru.skypro.homework.service.CommentsService;

import java.util.List;

/**
 * Реализация сервиса {@link CommentsService} для работы с комментариями к объявлениям.
 *
 * <p>Содержит бизнес‑логику для:
 * <ul>
 *   <li>получения списка комментариев конкретного объявления;</li>
 *   <li>добавления нового комментария с привязкой к автору и объявлению;</li>
 *   <li>удаления комментария с проверкой прав доступа;</li>
 *   <li>обновления текста комментария с проверкой прав.</li>
 * </ul>
 *
 * <p>Основные зависимости:
 * <ul>
 *   <li>{@link CommentsRepository} — для CRUD‑операций с сущностями комментариев;</li>
 *   <li>{@link AdsRepository} — для проверки существования объявления;</li>
 *   <li>{@link UsersRepository} — для получения данных автора комментария;</li>
 *   <li>{@link CommentMapper} — для преобразования между сущностями и DTO.</li>
 * </ul>
 *
 * <p>Класс аннотирован {@link Service}, что:
 * <ul>
 *   <li>регистрирует его как Spring‑компонент бизнес‑слоя;</li>
 *   <li>позволяет внедрять его через DI;</li>
 *   <li>обеспечивает интеграцию с транзакционным менеджментом Spring.</li>
 * </ul>
 *
 * @see Service
 * @see CommentsService
 * @see CommentsRepository
 * @see AdsRepository
 * @see UsersRepository
 * @see CommentMapper
 */
@Service
public class CommentsServiceImpl implements CommentsService {

    /**
     * Репозиторий для работы с сущностями комментариев {@link CommentEntity}.
     */
    private final CommentsRepository commentsRepository;

    /**
     * Репозиторий для работы с сущностями объявлений {@link AdEntity}.
     */
    private final AdsRepository adsRepository;

    /**
     * Репозиторий для работы с сущностями пользователей {@link UserEntity}.
     */
    private final UsersRepository usersRepository;

    /**
     * Маппер для преобразования между {@link CommentEntity} и {@link Comment}.
     */
    private final CommentMapper mapper;

    /**
     * Конструктор для внедрения зависимостей через Spring DI.
     *
     * @param commentsRepository репозиторий комментариев
     * @param adsRepository репозиторий объявлений
     * @param usersRepository репозиторий пользователей
     * @param mapper маппер для преобразования сущностей и DTO
     */
    public CommentsServiceImpl(CommentsRepository commentsRepository, AdsRepository adsRepository, UsersRepository usersRepository, CommentMapper mapper) {
        this.commentsRepository = commentsRepository;
        this.adsRepository = adsRepository;
        this.usersRepository = usersRepository;
        this.mapper = mapper;
    }

    /**
     * Получает список комментариев для указанного объявления.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Находит объявление по ID через {@link AdsRepository#findById}.</li>
     *   <li>Если объявление не найдено — возвращает {@code null}.</li>
     *   <li>Получает список комментариев из связи {@link AdEntity#getComments}.</li>
     *   <li>Преобразует каждую сущность {@link CommentEntity} в DTO {@link Comment} через {@link CommentMapper#toDto}.</li>
     *   <li>Формирует объект {@link Comments} с количеством и списком комментариев.</li>
     * </ol>
     *
     * @param id идентификатор объявления
     * @return объект {@link Comments} или {@code null}, если объявление не найдено
     * @see Comments
     * @see Comment
     */
    @Override
    public Comments getComments(int id) {
        AdEntity ad = adsRepository.findById(id).orElse(null);
        if (ad == null) {
            return null;
        }
        List<Comment> comments = ad.getComments().stream()
                .map(mapper::toDto)
                .toList();
        Comments result = new Comments();
        result.setCount(comments.size());
        result.setResults(comments);
        return result;
    }

    /**
     * Добавляет новый комментарий к указанному объявлению.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Проверяет существование объявления по ID.</li>
     *   <li>Находит пользователя по email через {@link UsersRepository#findByEmail}.</li>
     *   <li>Создаёт новую сущность {@link CommentEntity} с данными:</li>
     *     <ul>
     *       <li>имя автора — из {@link UserEntity#getFirstName};</li>
     *       <li>изображение — из {@link UserEntity#getImage};</li>
     *       <li>время создания — текущее время в миллисекундах;</li>
     *       <li>текст — из {@link CreateOrUpdateComment#getText};</li>
     *       <li>связи — с автором и объявлением.</li>
     *     </ul>
     *   <li>Сохраняет комментарий через {@link CommentsRepository#save}.</li>
     *   <li>Возвращает DTO {@link Comment}, преобразованный через {@link CommentMapper#toDto}.</li>
     * </ol>
     *
     * @param name email пользователя, добавляющего комментарий
     * @param id идентификатор объявления
     * @param properties DTO {@link CreateOrUpdateComment} с текстом комментария
     * @return объект {@link Comment} нового комментария или {@code null}, если объявление не найдено
     * @see CreateOrUpdateComment
     * @see Comment
     */
    @Override
    public Comment addComment(String name, int id, CreateOrUpdateComment properties) {
        AdEntity ad = adsRepository.findById(id).orElse(null);
        if (ad == null) {
            return null;
        }
        UserEntity user = usersRepository.findByEmail(name);
        CommentEntity comment = new CommentEntity();
        comment.setAuthorFirstName(user.getFirstName());
        comment.setAuthorImage(user.getImage());
        comment.setCreatedAt(System.currentTimeMillis());
        comment.setText(properties.getText());
        comment.setCommentAuthor(user);
        comment.setCommentAd(ad);
        commentsRepository.save(comment);
        return mapper.toDto(comment);
    }

    /**
     * Удаляет комментарий с проверкой прав доступа.
     *
     * <p>Возвращает код результата:
     * <ul>
     *   <li>0 — успешно удалено;</li>
     *   <li>1 — комментарий или объявление не найдены;</li>
     *   <li>2 — у пользователя нет прав (не автор и не администратор).</li>
     * </ul>
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Находит комментарий по ID через {@link CommentsRepository#findById}.</li>
     *   <li>Если комментарий не найден — возвращает 1.</li>
     *   <li>Получает пользователя по email.</li>
     *   <li>Проверяет права: администратор или автор комментария.</li>
     *   <li>Удаляет комментарий через {@link CommentsRepository#deleteById}, если права подтверждены.</li>
     * </ol>
     *
     * @param name email пользователя, пытающегося удалить комментарий
     * @param adId идентификатор объявления (для дополнительной проверки)
     * @param commentId идентификатор комментария
     * @return код результата (0, 1 или 2)
     */
    @Override
    public int deleteComment(String name, int adId, int commentId) {
        CommentEntity comment = commentsRepository.findById(commentId).orElse(null);
        if (comment == null) {
            return 1;
        }
        UserEntity user = usersRepository.findByEmail(name);
        if (!user.getRole().equals("ADMIN") && !comment.getCommentAuthor().getEmail().equals(name)) {
            return 2;
        }

        commentsRepository.deleteById(commentId);
        return 0;
    }

    /**
    * Обновляет текст существующего комментария с проверкой прав доступа.
    *
    * <p>Алгоритм:
    * <ol>
    *   <li>Находит комментарий по ID через {@link CommentsRepository#findById}.</li>
    *   <li>Если комментарий не найден — возвращает {@code null}.</li>
    *   <li>Получает пользователя по email.</li>
    *   <li>Проверяет права: администратор или автор комментария.</li>
    *   <li>Если права не подтверждены — выбрасывает {@link SecurityException}.</li>
    *   <li>Обновляет текст комментария из {@link CreateOrUpdateComment#getText}.</li>
    *   <li>Сохраняет изменения через {@link CommentsRepository#save}.</li>
    *   <li>Возвращает обновлённый DTO {@link Comment} через {@link CommentMapper#toDto}.</li>
    * </ol>
    *
    * @param name email пользователя, пытающегося обновить комментарий
    * @param properties DTO {@link CreateOrUpdateComment} с новым текстом комментария
    * @param commentId идентификатор комментария
    * @param adId идентификатор объявления (для контекста)
    * @return объект {@link Comment} с обновлёнными данными или {@code null},
    *         если комментарий не найден
    * @throws SecurityException если у пользователя нет прав на обновление (не автор и не администратор)
    * @see CreateOrUpdateComment
    * @see Comment
    * @see SecurityException
    */
    @Override
    public Comment updateComment(String name, CreateOrUpdateComment properties, int commentId, int adId) throws SecurityException {
        CommentEntity comment = commentsRepository.findById(commentId).orElse(null);
        if (comment == null) {
            return null;
        }

        UserEntity user = usersRepository.findByEmail(name);
        if (!user.getRole().equals("ADMIN") && !comment.getCommentAuthor().getEmail().equals(name)) {
            throw new SecurityException();
        }

        comment.setText(properties.getText());
        commentsRepository.save(comment);
        return mapper.toDto(comment);
    }
}
