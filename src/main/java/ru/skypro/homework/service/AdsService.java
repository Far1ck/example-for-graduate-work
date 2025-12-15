package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;

import java.io.IOException;

/**
 * Сервис для бизнес‑логики работы с объявлениями в системе.
 *
 * <p>Предоставляет методы для:
 * <ul>
 *   <li>получения списка всех объявлений;</li>
 *   <li>создания нового объявления (с изображением);</li>
 *   <li>получения детальной информации об объявлении;</li>
 *   <li>удаления объявления;</li>
 *   <li>обновления информации об объявлении;</li>
 *   <li>получения объявлений конкретного пользователя;</li>
 *   <li>обновления изображения объявления.</li>
 * </ul>
 *
 * <p>Каждый метод определяет контракт для соответствующей операции,
 * оставляя реализацию конкретным классам‑реализаторам.
 *
 * @see Ad
 * @see Ads
 * @see CreateOrUpdateAd
 * @see ExtendedAd
 * @see MultipartFile
 */
public interface AdsService {

    /**
     * Получает список всех объявлений в системе.
     *
     * @return объект {@link Ads}, содержащий:
     *         <ul>
     *           <li>{@code count} — общее количество объявлений;</li>
     *           <li>{@code results} — список объявлений (объекты {@link Ad}).</li>
     *         </ul>
     * @see Ads
     * @see Ad
     */
    Ads getAllAds();

    /**
     * Создаёт новое объявление с указанным изображением.
     *
     * @param name имя (логин) пользователя, создающего объявление
     * @param properties DTO {@link CreateOrUpdateAd} с данными объявления:
     *                   заголовок, цена, описание
     * @param image файл изображения объявления в формате {@link MultipartFile}
     * @return объект {@link Ad} с данными созданного объявления
     * @throws IOException если возникла ошибка при работе с файлом изображения
     * @see CreateOrUpdateAd
     * @see MultipartFile
     * @see Ad
     */
    Ad addAd(String name, CreateOrUpdateAd properties, MultipartFile image) throws IOException;

    /**
     * Получает детальную информацию об объявлении по его ID.
     *
     * @param id идентификатор объявления
     * @return объект {@link ExtendedAd} с полной информацией об объявлении
     *         или {@code null}, если объявление не найдено
     * @see ExtendedAd
     */
    ExtendedAd getAds(int id);

    /**
     * Удаляет объявление по его ID.
     *
     * @param name имя (логин) пользователя, пытающегося удалить объявление
     * @param id идентификатор объявления
     * @return код результата:
     *         <ul>
     *           <li>0 — успешное удаление;</li>
     *           <li>1 — объявление не найдено;</li>
     *           <li>2 — у пользователя нет прав на удаление.</li>
     *         </ul>
     */
    int removeAd(String name, int id) throws IOException;

    /**
     * Обновляет информацию об объявлении (заголовок, цену, описание).
     *
     * @param name имя (логин) пользователя, пытающегося обновить объявление
     * @param properties DTO {@link CreateOrUpdateAd} с новыми данными объявления
     * @param id идентификатор объявления
     * @return объект {@link Ad} с обновлёнными данными объявления
     *         или {@code null}, если объявление не найдено
     * @throws SecurityException если у пользователя нет прав на обновление объявления
     * @see CreateOrUpdateAd
     * @see Ad
     */
    Ad updateAds(String name, CreateOrUpdateAd properties, int id) throws SecurityException;

    /**
     * Получает список объявлений конкретного пользователя.
     *
     * @param name имя (логин) пользователя
     * @return объект {@link Ads}, содержащий:
     *         <ul>
     *           <li>{@code count} — количество объявлений пользователя;</li>
     *           <li>{@code results} — список его объявлений (объекты {@link Ad}).</li>
     *         </ul>
     * @see Ads
     * @see Ad
     */
    Ads getAdsMe(String name);

    /**
     * Обновляет изображение объявления.
     *
     * @param name имя (логин) пользователя, пытающегося обновить изображение
     * @param id идентификатор объявления
     * @param image новый файл изображения в формате {@link MultipartFile}
     * @return байтовый массив с данными обновлённого изображения
     * @throws IOException если возникла ошибка при работе с файлом изображения
     * @see MultipartFile
     */
    byte[] updateImage(String name, int id, MultipartFile image) throws IOException;
}
