package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.entity.AdEntity;

/**
 * Компонент‑маппер для преобразования между сущностью {@link AdEntity} и DTO {@link Ad}.
 *
 * <p>Обеспечивает двустороннее преобразование данных:
 * <ul>
 *   <li>из JPA‑сущности в DTO для передачи во внешний API (метод {@link #toDto});</li>
 *   <li>из DTO в сущность для сохранения в базе данных (метод {@link #toEntity}).</li>
 * </ul>
 *
 * <p>Основные особенности:
 * <ul>
 *   <li>помечен аннотацией {@link Component}, что делает его управляемым Spring‑контейнером;</li>
 *   <li>содержит нулевую проверку входных параметров для избежания NPE;</li>
 *   <li>выполняет прямое копирование полей с учётом различий в именовании и структуре.</li>
 * </ul>
 *
 * <p><b>Важно:</b> при преобразовании:
 * <ul>
 *   <li>поле {@code author} в DTO заполняется ID автора из связанной сущности {@code adAuthor} сущности {@link AdEntity};</li>
 *   <li>поле {@code pk} в DTO соответствует полю {@code id} в сущности.</li>
 * </ul>
 *
 * @see Component
 * @see Ad
 * @see AdEntity
 */
@Component
public class AdMapper {

    /**
     * Преобразует сущность {@link AdEntity} в DTO {@link Ad}.
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Проверяет, что входной параметр не равен {@code null}.</li>
     *   <li>Создаёт новый экземпляр {@link Ad}.</li>
     *   <li>Копирует поля из сущности в DTO с учётом соответствий:</li>
     *     <ul>
     *       <li>{@code adAuthor.id} → {@code author};</li>
     *       <li>{@code image} → {@code image};</li>
     *       <li>{@code id} → {@code pk};</li>
     *       <li>{@code price} → {@code price};</li>
     *       <li>{@code title} → {@code title}.</li>
     *     </ul>
     *   <li>Возвращает заполненный DTO.</li>
     * </ol>
     *
     * @param entity сущность {@link AdEntity}, подлежащая преобразованию
     * @return экземпляр {@link Ad}, заполненный данными из сущности,
     *         или {@code null}, если входной параметр был {@code null}
     * @see Ad#setAuthor(int)
     * @see Ad#setImage(String)
     * @see Ad#setPk(int)
     * @see Ad#setPrice(int)
     * @see Ad#setTitle(String)
     */
    public Ad toDto(AdEntity entity) {
        if (entity == null) {
            return null;
        }
        Ad dto = new Ad();
        dto.setAuthor(entity.getAdAuthor().getId());
        dto.setImage(entity.getImage());
        dto.setPk(entity.getId());
        dto.setPrice(entity.getPrice());
        dto.setTitle(entity.getTitle());
        return dto;
    }

    /**
     * Преобразует DTO {@link Ad} в сущность {@link AdEntity}.
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Проверяет, что входной параметр не равен {@code null}.</li>
     *   <li>Создаёт новый экземпляр {@link AdEntity}.</li>
     *   <li>Копирует поля из DTO в сущность с учётом соответствий:</li>
     *     <ul>
     *       <li>{@code image} → {@code image};</li>
     *       <li>{@code pk} → {@code id};</li>
     *       <li>{@code price} → {@code price};</li>
     *       <li>{@code title} → {@code title}.</li>
     *     </ul>
     *   <li>Возвращает заполненную сущность.</li>
     * </ol>
     *
     * <p><b>Примечание:</b> поле {@code adAuthor} в сущности не заполняется —
     * его необходимо установить отдельно при сохранении, если требуется связь с пользователем.
     *
     * @param dto DTO {@link Ad}, подлежащий преобразованию
     * @return экземпляр {@link AdEntity}, заполненный данными из DTO,
     *         или {@code null}, если входной параметр был {@code null}
     * @see AdEntity#setImage(String)
     * @see AdEntity#setId(Integer) 
     * @see AdEntity#setPrice(int)
     * @see AdEntity#setTitle(String)
     */
    public AdEntity toEntity(Ad dto) {
        if (dto == null) {
            return null;
        }
        AdEntity entity = new AdEntity();
        entity.setImage(dto.getImage());
        entity.setId(dto.getPk());
        entity.setPrice(dto.getPrice());
        entity.setTitle(dto.getTitle());
        return entity;
    }
}
