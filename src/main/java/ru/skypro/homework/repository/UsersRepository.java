package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.UserEntity;

/**
 * Репозиторий для работы с сущностями пользователей ({@link UserEntity}) в базе данных.
 *
 * <p>Предоставляет CRUD‑операции через наследование от {@link JpaRepository}, а также
 * дополнительный метод для поиска пользователя по email.
 *
 * <p>Основные особенности:
 * <ul>
 *   <li>помечен аннотацией {@link Repository}, что делает его управляемым Spring‑контейнером;</li>
 *   <li>наследует стандартный набор методов JPA (save, findById, delete и др.);</li>
 *   <li>добавляет специализированный метод {@code findByEmail} для поиска по полю email.</li>
 * </ul>
 *
 * @see Repository
 * @see JpaRepository
 * @see UserEntity
 */
@Repository
public interface UsersRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByEmail(String email);
}
