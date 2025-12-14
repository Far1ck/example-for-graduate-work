package ru.skypro.homework.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * Сущность объявления (товара/услуги), соответствующая таблице {@code ads} в базе данных.
 *
 * <p>Описывает структуру объявления, включая:
 * <ul>
 *   <li>основные атрибуты (заголовок, описание, цена, изображение);</li>
 *   <li>связь с автором объявления ({@link UserEntity});</li>
 *   <li>список комментариев к объявлению ({@link CommentEntity}).</li>
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
 * @see CommentEntity
 */
@Entity
@Table(name = "ads")
@Data
public class AdEntity {

    /**
     * Путь к изображению объявления (относительный или абсолютный URL).
     *
     * <p>Может быть пустым, если изображение не загружено.
     */
    private String image;

    /**
     * Уникальный идентификатор объявления в системе.
     *
     * <p>Генерируется автоматически СУБД при сохранении новой записи.
     * Стратегия генерации — {@link GenerationType#IDENTITY} (автоинкремент).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Стоимость товара/услуги, указанная в объявлении.
     *
     * <p>Хранится как целое число (например, в копейках или минимальной денежной единице).
     */
    private int price;

    /**
     * Заголовок объявления, кратко описывающий товар/услугу.
     *
     * <p>Должен быть заполнен при создании объявления.
     */
    private String title;

    /**
     * Подробное описание товара/услуги.
     *
     * <p>Может содержать дополнительную информацию, условия продажи и т. п.
     */
    private String description;

    /**
     * Список комментариев, оставленных к данному объявлению.
     *
     * <p>Связь один‑ко‑многим с сущностью {@link CommentEntity}.
     * <ul>
     *   <li>{@code mappedBy = "commentAd"} — обратная сторона связи;</li>
     *   <li>{@code cascade = CascadeType.ALL} — все операции (сохранение, удаление) распространяются на связанные комментарии;</li>
     *   <li>{@code orphanRemoval = true} — удаление комментария из списка приводит к удалению записи из БД;</li>
     *   <li>{@code fetch = FetchType.EAGER} — комментарии загружаются сразу при загрузке объявления.</li>
     * </ul>
     *
     * @see CommentEntity
     */
    @OneToMany(mappedBy = "commentAd", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CommentEntity> comments;

    /**
     * Автор объявления — пользователь, создавший запись.
     *
     * <p>Связь многие‑к‑одному с сущностью {@link UserEntity}.
     * <ul>
     *   <li>{@code fetch = FetchType.LAZY} — загрузка автора выполняется только при явном обращении;</li>
     *   <li>{@code JoinColumn(name = "author")} — внешний ключ в таблице {@code ads}, ссылающийся на {@code id} пользователя.</li>
     * </ul>
     *
     * @see UserEntity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    private UserEntity adAuthor;
}

