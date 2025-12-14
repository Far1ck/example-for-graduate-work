package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.AdEntity;

/**
 * Репозиторий для работы с сущностями объявлений ({@link AdEntity}) в базе данных.
 *
 * <p>Предоставляет стандартный набор CRUD‑операций через наследование от
 * {@link JpaRepository}, позволяя выполнять:
 * <ul>
 *   <li>создание новых объявлений;</li>
 *   <li>чтение объявлений по идентификатору;</li>
 *   <li>обновление существующих объявлений;</li>
 *   <li>удаление объявлений;</li>
 *   <li>массовые операции (например, получение списка всех объявлений).</li>
 * </ul>
 *
 * <p>Основные особенности:
 * <ul>
 *   <li>помечен аннотацией {@link Repository}, что делает его управляемым Spring‑контейнером;</li>
 *   <li>наследует все стандартные методы JPA (save, findById, delete и др.);</li>
 *   <li>не содержит дополнительных пользовательских методов (в текущей реализации используется только базовый функционал).</li>
 * </ul>
 *
 * @see Repository
 * @see JpaRepository
 * @see AdEntity
 * @see org.springframework.data.jpa.repository.JpaRepository#save(Object) 
 * @see org.springframework.data.jpa.repository.JpaRepository#findById(Object) 
 * @see org.springframework.data.jpa.repository.JpaRepository#deleteById(Object) 
 */
@Repository
public interface AdsRepository extends JpaRepository <AdEntity, Integer> {
}
