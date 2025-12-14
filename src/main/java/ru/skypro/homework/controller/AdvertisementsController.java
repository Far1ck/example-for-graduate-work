package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.service.AdsService;

import java.io.IOException;

/**
 * Контроллер для управления объявлениями в системе.
 *
 * <p>Предоставляет REST‑конечные точки для:
 * <ul>
 *   <li>получения списка всех объявлений;</li>
 *   <li>создания нового объявления (с изображением);</li>
 *   <li>получения детальной информации об объявлении;</li>
 *   <li>удаления объявления;</li>
 *   <li>обновления информации об объявлении;</li>
 *   <li>получения объявлений текущего пользователя;</li>
 *   <li>обновления изображения объявления.</li>
 * </ul>
 *
 * <p>Основные особенности:
 * <ul>
 *   <li>помечен аннотацией {@link RestController}, что автоматически делает все методы возвращающими данные в формате JSON;</li>
 *   <li>имеет базовый путь {@code /ads} (задаётся через {@link RequestMapping});</li>
 *   <li>поддерживает CORS‑запросы с источника {@code http://localhost:3000};</li>
 *   <li>использует внедрение зависимостей через конструктор (аннотация {@link RequiredArgsConstructor});</li>
 *   <li>для документации API используются аннотации Swagger/OpenAPI ({@link Operation}, {@link ApiResponse}).</li>
 * </ul>
 *
 * @see CrossOrigin
 * @see RestController
 * @see RequestMapping
 * @see RequiredArgsConstructor
 * @see AdsService
 */
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdvertisementsController {

    /**
     * Сервис для бизнес‑логики работы с объявлениями.
     *
     * <p>Используется для:
     * <ul>
     *   <li>получения списков объявлений;</li>
     *   <li>создания, обновления и удаления объявлений;</li>
     *   <li>работы с изображениями объявлений.</li>
     * </ul>
     *
     * @see AdsService
     */
    private final AdsService adsService;

    /**
     * Получает список всех объявлений в системе.
     *
     * <p>Endpoint: {@code GET /ads}
     *
     * @return {@link ResponseEntity} с объектом {@link Ads}, содержащим:
     *         <ul>
     *           <li>{@code count} — общее количество объявлений;</li>
     *           <li>{@code results} — список объявлений (объекты {@link Ad}).</li>
     *         </ul>
     *         Статус {@code 200 OK}.
     */
    @GetMapping
    public ResponseEntity<Ads> getAllAds() {
        return ResponseEntity.ok(adsService.getAllAds());
    }

    /**
     * Добавляет новое объявление с изображением.
     *
     * <p>Endpoint: {@code POST /ads} (с multipart‑формой)
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Проверяет, что файл изображения не пуст и имеет MIME‑тип {@code image/*}.</li>
     *   <li>Передаёт данные в сервис {@link AdsService#addAd} для создания объявления.</li>
     *   <li>Возвращает созданное объявление со статусом {@code 201 Created}.</li>
     * </ol>
     *
     * @param properties DTO {@link CreateOrUpdateAd} с данными объявления (заголовок, цена, описание)
     * @param image файл изображения объявления в формате {@link MultipartFile}
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link ResponseEntity} со статусом:
     *         <ul>
     *           <li>{@code 201 Created} при успешном создании, тело — объект {@link Ad};</li>
     *           <li>{@code 400 Bad Request} если файл пуст или имеет неверный тип;</li>
     *           <li>{@code 401 Unauthorized} если пользователь не аутентифицирован.</li>
     *         </ul>
     * @throws IOException если возникла ошибка при работе с файлом
     * @see CreateOrUpdateAd
     * @see MultipartFile
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Добавление объявления",
            tags = {"Объявления"},
            operationId = "addAd"
    )
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<Ad> addAd(@RequestPart("properties") @Valid CreateOrUpdateAd properties,
                                    @RequestPart("image") MultipartFile image,
                                    Authentication authentication) throws IOException {
        if (image.isEmpty() || !image.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().build();
        }
        Ad ad = adsService.addAd(authentication.getName(), properties, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(ad);
    }

    /**
     * Получает детальную информацию об объявлении по его ID.
     *
     * <p>Endpoint: {@code GET /ads/{id}}
     *
     * @param id идентификатор объявления
     * @return {@link ResponseEntity} со статусом:
     *         <ul>
     *           <li>{@code 200 OK} при нахождении объявления, тело — объект {@link ExtendedAd};</li>
     *           <li>{@code 404 Not Found} если объявление не существует;</li>
     *           <li>{@code 401 Unauthorized} если пользователь не аутентифицирован.</li>
     *         </ul>
     * @see ExtendedAd
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Получение информации об объявлении",
            tags = {"Объявления"},
            operationId = "getAds"
    )
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<ExtendedAd> getAds(@PathVariable("id") int id) {
        ExtendedAd result = adsService.getAds(id);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Удаляет объявление по его ID.
     *
     * <p>Endpoint: {@code DELETE /ads/{id}}
     *
     * @param id идентификатор объявления
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link ResponseEntity} со статусом:
     *         <ul>
     *           <li>{@code 204 No Content} при успешном удалении;</li>
     *           <li>{@code 404 Not Found} если объявление не существует;</li>
     *           <li>{@code 403 Forbidden} если пользователь не имеет прав на удаление;</li>
     *           <li>{@code 401 Unauthorized} если пользователь не аутентифицирован.</li>
     *         </ul>
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаление объявления",
            tags = {"Объявления"},
            operationId = "removeAd"
    )
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<Void> removeAd(@PathVariable("id") int id, Authentication authentication) {
        int result = adsService.removeAd(authentication.getName(), id);
        if (result == 1) {
            return ResponseEntity.notFound().build();
        }
        if (result == 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновляет информацию об объявлении (заголовок, цену, описание).
     *
     * <p>Endpoint: {@code PATCH /ads/{id}}
     *
     * @param id идентификатор объявления
     * @param properties DTO {@link CreateOrUpdateAd} с новыми данными объявления
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link ResponseEntity} со статусом:
     *         <ul>
     *           <li>{@code 200 OK} при успешном обновлении, тело — объект {@link Ad};</li>
     *           <li>{@code 404 Not Found} если объявление не существует;</li>
     *           <li>{@code 403 Forbidden} если пользователь не имеет прав на обновление;</li>
     *           <li>{@code 401 Unauthorized} если пользователь не аутентифицирован.</li>
     *         </ul>
     * @see CreateOrUpdateAd
     * @see Ad
     */
    @PatchMapping("/{id}")
    @Operation(
            summary = "Обновление информации об объявлении",
            tags = {"Объявления"},
            operationId = "updateAds"
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<Ad> updateAds(@PathVariable("id") int id,
                                        @RequestBody CreateOrUpdateAd properties,
                                        Authentication authentication) {
        try {
            Ad ad = adsService.updateAds(authentication.getName(), properties, id);
            if (ad == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ad);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Получает список объявлений текущего авторизованного пользователя.
     *
     * <p>Endpoint: {@code GET /ads/me}
     *
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link ResponseEntity} с объектом {@link Ads}, содержащим:
     *         <ul>
     *           <li>{@code count} — количество объявлений пользователя;</li>
     *           <li>{@code results} — список его объявлений (объекты {@link Ad}).</li>
     *         </ul>
     *         Статус {@code 200 OK}.
     * @see Ads
     * @see Ad
     */
    @GetMapping("/me")
    @Operation(
            summary = "Получение объявлений авторизованного пользователя",
            tags = {"Объявления"},
            operationId = "getAdsMe"
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<Ads> getAdsMe(Authentication authentication) {
        Ads ads = adsService.getAdsMe(authentication.getName());
        return ResponseEntity.ok(ads);
    }

    /**
     * Обновляет изображение объявления.
     *
     * <p>Endpoint: {@code PATCH /ads/{id}/image} (с multipart‑формой)
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Проверяет, что файл изображения не пуст и имеет MIME‑тип {@code image/*}.</li>
     *   <li>Передаёт данные в сервис {@link AdsService#updateImage} для обновления изображения.</li>
     *   <li>Возвращает обновлённое изображение.</li>
     * </ol>
     *
     * @param id идентификатор объявления
     * @param image новый файл изображения в формате {@link MultipartFile}
     * @param authentication объект аутентификации текущего пользователя
     * @return {@link ResponseEntity} со статусом:
     *         <ul>
     *           <li>{@code 200 OK} при успешном обновлении, тело — байтовый массив изображения;</li>
     *           <li>{@code 400 Bad Request} если файл пуст или имеет неверный тип;</li>
     *           <li>{@code 404 Not Found} если объявление не существует;</li>
     *           <li>{@code 403 Forbidden} если пользователь не имеет прав на обновление;</li>
     *           <li>{@code 401 Unauthorized} если пользователь не аутентифицирован.</li>
     *         </ul>
     * @throws IOException если возникла ошибка при работе с файлом
     * @see MultipartFile
     */
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Обновление картинки объявления",
            tags = {"Объявления"},
            operationId = "updateImage"
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<byte[]> updateImage(@PathVariable("id") int id,
                                              @RequestParam("image") MultipartFile image,
                                              Authentication authentication) throws IOException {
        if (image.isEmpty() || !image.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().build();
        }
        try {
            byte[] updatedImage = adsService.updateImage(authentication.getName(), id, image);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(updatedImage);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
