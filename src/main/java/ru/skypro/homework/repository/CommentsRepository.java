package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.CommentEntity;

/**
 * Репозиторий для работы с сущностями комментариев ({@link CommentEntity}) в базе данных.
 *
 * <p>Предоставляет стандартный набор CRUD‑операций и возможность расширения функционала
 * через методы Spring Data JPA. Основан на интерфейсе {@link JpaRepository},
 * что обеспечивает интеграцию с JPA и Hibernate.
 *
 * <p>Основные возможности:
 * <ul>
 *   <li>создание, чтение, обновление и удаление комментариев;</li>
 *   <li>поиск по идентификатору;</li>
 *   <li>пагинация и сортировка результатов;</li>
 *   <li>автоматическая генерация реализаций методов по именам (на основе соглашений Spring Data).</li>
 * </ul>
 *
 * <p>Аннотирован {@link Repository}, что:
 * <ul>
 *   <li>помещает класс в контекст Spring как компонент доступа к данным;</li>
 *   <li>позволяет Spring автоматически обнаруживать и настраивать репозиторий;</li>
 *   <li>обеспечивает интеграцию с транзакционным менеджментом Spring.</li>
 * </ul>
 *
 * @see Repository
 * @see JpaRepository
 * @see CommentEntity
 * @see org.springframework.data.jpa.repository.JpaRepository#save(Object)
 * @see org.springframework.data.jpa.repository.JpaRepository#findById(Object)
 * @see org.springframework.data.jpa.repository.JpaRepository#deleteById(Object)
 */
@Repository
public interface CommentsRepository extends JpaRepository<CommentEntity, Integer> {
}
