package ru.skypro.homework.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Сущность комментария, соответствующая таблице {@code comments} в базе данных.
 *
 * <p>Описывает структуру комментария к объявлению, включая:
 * <ul>
 *   <li>информацию об авторе комментария (имя, аватар);</li>
 *   <li>текст сообщения и время создания;</li>
 *   <li>связи с автором ({@link UserEntity}) и объявлением ({@link AdEntity}), к которому относится комментарий.</li>
 * </ul>
 *
 * <p><b>Примечание:</b> класс аннотирован {@link Data} от Lombok, что автоматически генерирует:
 * <ul>
 *   <li>геттеры и сеттеры для всех полей;</li>
 *   <li>реализацию {@code equals()} и {@code hashCode()};</li>
 *   <li>реализацию {@code toString()};</li>
 *   <li>конструктор по умолчанию.</li>
 * </ul>
 *
 * @see Entity
 * @see Table
 * @see Data
 * @see UserEntity
 * @see AdEntity
 */
@Entity
@Table(name = "comments")
@Data
public class CommentEntity {

    /**
     * Путь к аватару автора комментария (относительный или абсолютный URL).
     *
     * <p>В базе данных хранится в колонке {@code author_image}.
     * Может быть пустым, если пользователь не загрузил изображение.
     */
    @Column(name = "author_image")
    private String authorImage;

    /**
     * Имя автора комментария.
     *
     * <p>В базе данных хранится в колонке {@code author_first_name}.
     * Используется для отображения в интерфейсе без обращения к сущности пользователя.
     */
    @Column(name = "author_first_name")
    private String authorFirstName;

    /**
     * Временная метка создания комментария (в миллисекундах с эпохи Unix).
     *
     * <p>В базе данных хранится в колонке {@code created_at}.
     * Позволяет сортировать комментарии по времени и отображать дату создания.
     */
    @Column(name = "created_at")
    private long createdAt;

    /**
     * Уникальный идентификатор комментария в системе.
     *
     * <p>Генерируется автоматически СУБД при сохранении новой записи.
     * Стратегия генерации — {@link GenerationType#IDENTITY} (автоинкремент).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Текст комментария, оставленный пользователем.
     *
     * <p>Должен быть заполнен при создании записи.
     * Ограничения на длину определяются схемой БД.
     */
    private String text;

    /**
     * Автор комментария — пользователь, оставивший сообщение.
     *
     * <p>Связь многие‑к‑одному с сущностью {@link UserEntity}.
     * <ul>
     *   <li>{@code fetch = FetchType.LAZY} — загрузка автора выполняется только при явном обращении;</li>
     *   <li>{@code JoinColumn(name = "author")} — внешний ключ в таблице {@code comments}, ссылающийся на {@code id} пользователя.</li>
     * </ul>
     *
     * @see UserEntity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    private UserEntity commentAuthor;

    /**
     * Объявление, к которому относится комментарий.
     *
     * <p>Связь многие‑к‑одному с сущностью {@link AdEntity}.
     * <ul>
     *   <li>{@code fetch = FetchType.LAZY} — загрузка объявления выполняется только при явном обращении;</li>
     *   <li>{@code JoinColumn(name = "ad")} — внешний ключ в таблице {@code comments}, ссылающийся на {@code id} объявления.</li>
     * </ul>
     *
     * @see AdEntity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad")
    private AdEntity commentAd;
}
