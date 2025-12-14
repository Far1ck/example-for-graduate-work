package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.entity.CommentEntity;

/**
 * Маппер для преобразования между сущностью {@link CommentEntity} и DTO {@link Comment}.
 *
 * <p>Обеспечивает двустороннее преобразование данных:
 * <ul>
 *   <li>из JPA‑сущности в объект передачи данных (DTO) для API;</li>
 *   <li>из DTO в сущность для сохранения в базе данных.</li>
 * </ul>
 *
 * <p>Основные цели использования:
 * <ul>
 *   <li>разделение слоя хранения данных (entities) и слоя передачи данных (DTO);</li>
 *   <li>скрытие внутренней структуры сущностей от API‑клиентов;</li>
 *   <li>обеспечение гибкости при изменении модели данных без влияния на API.</li>
 * </ul>
 *
 * <p>Класс аннотирован {@link Component}, что:
 * <ul>
 *   <li>регистрирует его как Spring‑компонент;</li>
 *   <li>позволяет внедрять его в другие компоненты через DI;</li>
 *   <li>обеспечивает управление жизненным циклом объекта контейнером Spring.</li>
 * </ul>
 *
 * @see Component
 * @see Comment
 * @see CommentEntity
 */
@Component
public class CommentMapper {

    /**
     * Преобразует сущность {@link CommentEntity} в DTO {@link Comment}.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Проверяет, что входная сущность не {@code null}.</li>
     *   <li>Создаёт новый объект {@link Comment}.</li>
     *   <li>Копирует поля из сущности в DTO, выполняя необходимые преобразования:</li>
     *     <ul>
     *       <li>ID автора извлекается из связанной сущности {@link CommentEntity#getCommentAuthor};</li>
     *       <li>остальные поля копируются напрямую.</li>
     *     </ul>
     *   <li>Возвращает заполненный DTO.</li>
     * </ol>
     *
     * @param entity исходная сущность {@link CommentEntity}, может быть {@code null}
     * @return объект {@link Comment} с данными из сущности или {@code null}, если вход {@code null}
     * @see Comment
     * @see CommentEntity
     */
    public Comment toDto(CommentEntity entity) {
        if (entity == null) {
            return null;
        }
        Comment dto = new Comment();
        dto.setAuthor(entity.getCommentAuthor().getId());
        dto.setAuthorImage(entity.getAuthorImage());
        dto.setAuthorFirstName(entity.getAuthorFirstName());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setPk(entity.getId());
        dto.setText(entity.getText());
        return dto;
    }

    /**
     * Преобразует DTO {@link Comment} в сущность {@link CommentEntity}.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Проверяет, что входной DTO не {@code null}.</li>
     *   <li>Создаёт новую сущность {@link CommentEntity}.</li>
     *   <li>Копирует поля из DTO в сущность:</li>
     *     <ul>
     *       <li>ID сущности устанавливается из поля {@code pk} DTO;</li>
     *       <li>остальные поля копируются напрямую.</li>
     *     </ul>
     *   <li>Возвращает заполненную сущность.</li>
     * </ol>
     *
     * <p><b>Примечание:</b> поле {@code commentAuthor} (связь с пользователем) не заполняется —
     * это должно быть сделано отдельно на уровне сервиса.
     *
     * @param dto входной DTO {@link Comment}, может быть {@code null}
     * @return сущность {@link CommentEntity} с данными из DTO или {@code null}, если вход {@code null}
     * @see Comment
     * @see CommentEntity
     */
    public CommentEntity toEntity(Comment dto) {
        if (dto == null) {
            return null;
        }
        CommentEntity entity = new CommentEntity();
        entity.setAuthorImage(dto.getAuthorImage());
        entity.setAuthorFirstName(dto.getAuthorFirstName());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setId(dto.getPk());
        entity.setText(dto.getText());
        return entity;
    }
}
