package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Контроллер для обработки запросов на получение изображений.
 *
 * <p>Предоставляет REST‑конечную точку для загрузки изображений по имени файла.
 * Поддерживает основные форматы: PNG, JPEG, GIF и общие типы изображений (image/*).
 *
 * <p>Основные особенности:
 * <ul>
 *   <li>работает как REST‑контроллер (аннотация {@link RestController});</li>
 *   <li>использует Lombok для генерации конструктора ({@link RequiredArgsConstructor});</li>
 *   <li>путь к директории с изображениями конфигурируется через свойство приложения ({@code app.images.dir}).</li>
 * </ul>
 *
 * @see RestController
 * @see RequiredArgsConstructor
 * @see Value
 */
@RestController
@RequiredArgsConstructor
public class ImagesController {

    /**
     * Путь к директории, где хранятся изображения.
     *
     * <p>Значение подставляется из конфигурационного свойства {@code app.images.dir}
     * через аннотацию {@link Value}. Пример значения в properties‑файле:
     * <pre>
     * app.images.dir=/var/images/uploads
     * </pre>
     *
     * @see Value
     */
    @Value("${app.images.dir}")
    private String imagesPath;

    /**
     * Обрабатывает GET‑запрос на получение изображения по имени файла.
     *
     * <p>Endpoint: {@code GET /images/{image}}
     *
     * <p>Алгоритм работы:
     * <ol>
     *   <li>Формирует полный путь к файлу:
     *     <ul>
     *       <li>берёт базовую директорию из {@link #imagesPath};</li>
     *       <li>добавляет имя файла из параметра {@code image}.</li>
     *     </ul>
     *   </li>
     *   <li>Читает содержимое файла в массив байт с помощью {@link Files#readAllBytes}.</li>
     *   <li>Возвращает ответ с содержимым файла и HTTP‑статусом 200 OK.</li>
     * </ol>
     *
     * <p>Поддерживаемые MIME‑типы (указаны в атрибуте {@code produces}):
     * <ul>
     *   <li>{@code image/png} — PNG‑изображения;</li>
     *   <li>{@code image/jpeg} — JPEG‑изображения;</li>
     *   <li>{@code image/gif} — GIF‑изображения;</li>
     *   <li>{@code image/*} — любые изображения (общий тип).</li>
     * </ul>
     *
     * @param image имя файла изображения (из URL‑пути)
     * @return {@link ResponseEntity} с массивом байт (содержимое файла) и HTTP‑заголовками,
     *         указывающими тип содержимого
     * @throws IOException если файл не найден или произошла ошибка чтения
     * @see GetMapping
     * @see PathVariable
     * @see ResponseEntity
     * @see Files#readAllBytes(Path)
     */
    @GetMapping(value = "/images/{image}", produces = {
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_GIF_VALUE,
            "image/*"
    })
    public ResponseEntity<byte[]> getImage(@PathVariable("image") String image) throws IOException {
        Path filePath = Paths.get(imagesPath);
        Path resultPath = filePath.resolve(image);
        return ResponseEntity.ok(Files.readAllBytes(resultPath));
    }
}
