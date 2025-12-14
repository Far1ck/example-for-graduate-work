package ru.skypro.homework.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * Сущность пользователя системы, соответствующая таблице {@code users} в базе данных.
 *
 * <p>Описывает структуру учётной записи пользователя, включая:
 * <ul>
 *   <li>основные персональные данные (email, имя, фамилия, телефон);</li>
 *   <li>информацию для аутентификации (пароль, статус активности);</li>
 *   <li>роль пользователя в системе;</li>
 *   <li>ссылку на аватар (image);</li>
 *   <li>ассоциации с объявлениями и комментариями пользователя.</li>
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
 * @see CommentEntity
 * @see AdEntity
 */
@Entity
@Table(name = "users")
@Data
public class UserEntity {

    /**
     * Уникальный идентификатор пользователя в системе.
     *
     * <p>Генерируется автоматически СУБД при сохранении новой записи.
     * Стратегия генерации — {@link GenerationType#IDENTITY} (автоинкремент).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Электронный адрес пользователя, используемый как логин для аутентификации.
     *
     * <p>Должен быть уникальным в пределах системы.
     */
    private String email;

    /**
     * Имя пользователя.
     *
     * <p>В базе данных хранится в колонке {@code first_name}.
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * Фамилия пользователя.
     *
     * <p>В базе данных хранится в колонке {@code last_name}.
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * Контактный телефон пользователя.
     */
    private String phone;

    /**
     * Роль пользователя в системе (например, "USER", "ADMIN").
     *
     * <p>Определяет набор прав доступа и разрешённых действий.
     */
    private String role;

    /**
     * Путь к файлу аватара пользователя (относительный или абсолютный URL).
     *
     * <p>Может быть пустым, если пользователь не загрузил изображение.
     */
    private String image;

    /**
     * Хешированный пароль пользователя.
     *
     * <p><b>Важно:</b> хранится только в зашифрованном виде (например, через BCrypt).
     * Никогда не сохраняется в открытом виде.
     */
    private String password;

    /**
     * Флаг активности учётной записи.
     *
     * <p>Значение {@code true} означает, что пользователь может входить в систему.
     * {@code false} — учётная запись деактивирована (например, администратором).
     */
    private boolean enabled;

    /**
     * Список комментариев, оставленных пользователем.
     *
     * <p>Связь один‑ко‑многим с сущностью {@link CommentEntity}.
     * <ul>
     *   <li>{@code mappedBy = "commentAuthor"} — обратная сторона связи;</li>
     *   <li>{@code cascade = CascadeType.ALL} — все операции (сохранение, удаление) распространяются на связанные комментарии;</li>
     *   <li>{@code orphanRemoval = true} — удаление комментария из списка приводит к удалению записи из БД;</li>
     *   <li>{@code fetch = FetchType.EAGER} — комментарии загружаются сразу при загрузке пользователя.</li>
     * </ul>
     *
     * @see CommentEntity
     */
    @OneToMany(mappedBy = "commentAuthor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CommentEntity> comments;

    /**
     * Список объявлений, созданных пользователем.
     *
     * <p>Связь один‑ко‑многим с сущностью {@link AdEntity}.
     * <ul>
     *   <li>{@code mappedBy = "adAuthor"} — обратная сторона связи;</li>
     *   <li>{@code cascade = CascadeType.ALL} — все операции распространяются на связанные объявления;</li>
     *   <li>{@code orphanRemoval = true} — удаление объявления из списка приводит к удалению записи из БД;</li>
     *   <li>{@code fetch = FetchType.EAGER} — объявления загружаются сразу при загрузке пользователя.</li>
     * </ul>
     *
     * @see AdEntity
     */
    @OneToMany(mappedBy = "adAuthor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AdEntity> ads;
}
