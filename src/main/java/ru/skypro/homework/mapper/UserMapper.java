package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;

/**
 * Компонент-маппер для преобразования между сущностью {@link UserEntity} и DTO {@link User}.
 *
 * <p>Обеспечивает двустороннее преобразование данных:
 * <ul>
 *   <li>из JPA‑сущности в DTO для передачи во внешний API;</li>
 *   <li>из DTO в сущность для сохранения в базе данных.</li>
 * </ul>
 *
 * <p>Основные особенности:
 * <ul>
 *   <li>помечен аннотацией {@link Component}, что делает его управляемым Spring‑контейнером;</li>
 *   <li>обрабатывает преобразование поля {@code role} между строкой (в сущности) и enum {@link Role} (в DTO);</li>
 *   <li>корректно обрабатывает {@code null}‑значения на входе.</li>
 * </ul>
 *
 * @see Component
 * @see UserEntity
 * @see User
 * @see Role
 */
@Component
public class UserMapper {

    /**
     * Преобразует сущность {@link UserEntity} в DTO {@link User}.
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Проверяет, что входной объект не равен {@code null}.</li>
     *   <li>Создаёт новый экземпляр {@link User}.</li>
     *   <li>Копирует все поля из сущности в DTO.</li>
     *   <li>Преобразует поле {@code role} из строки в enum {@link Role} через {@link Role#valueOf}.</li>
     *   <li>Возвращает заполненный DTO.</li>
     * </ol>
     *
     * @param entity исходная сущность {@link UserEntity} из базы данных
     * @return экземпляр {@link User} с заполненными полями или {@code null},
     *         если входной объект был {@code null}
     * @see UserEntity#getId()
     * @see UserEntity#getEmail()
     * @see UserEntity#getFirstName()
     * @see UserEntity#getLastName()
     * @see UserEntity#getPhone()
     * @see UserEntity#getRole()
     * @see UserEntity#getImage()
     */
    public User toDto(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        User dto = new User();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setPhone(entity.getPhone());
        dto.setRole(Role.valueOf(entity.getRole()));
        dto.setImage(entity.getImage());
        return dto;
    }

    /**
     * Преобразует DTO {@link User} в сущность {@link UserEntity}.
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Проверяет, что входной DTO не равен {@code null}.</li>
     *   <li>Создаёт новый экземпляр {@link UserEntity}.</li>
     *   <li>Копирует все поля из DTO в сущность.</li>
     *   <li>Преобразует поле {@code role} из enum {@link Role} в строку через {@link Enum#name()}.</li>
     *   <li>Возвращает заполненную сущность.</li>
     * </ol>
     *
     * @param dto входной DTO {@link User} из API
     * @return экземпляр {@link UserEntity} с заполненными полями или {@code null},
     *         если входной DTO был {@code null}
     * @see User#getId()
     * @see User#getEmail()
     * @see User#getFirstName()
     * @see User#getLastName()
     * @see User#getPhone()
     * @see User#getRole()
     * @see User#getImage()
     */
    public UserEntity toEntity(User dto) {
        if (dto == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(dto.getId());
        entity.setEmail(dto.getEmail());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setPhone(dto.getPhone());
        entity.setRole(dto.getRole().name());
        entity.setImage(dto.getImage());
        return entity;
    }
}
