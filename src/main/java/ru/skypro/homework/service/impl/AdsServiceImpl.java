package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.UsersRepository;
import ru.skypro.homework.service.AdsService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Random;

/**
 * Реализация сервиса {@link AdsService} для работы с объявлениями в системе.
 *
 * <p>Содержит бизнес‑логику для:
 * <ul>
 *   <li>получения списков объявлений (всех или пользователя);</li>
 *   <li>создания новых объявлений с загрузкой изображений;</li>
 *   <li>получения детальной информации об объявлении;</li>
 *   <li>удаления объявлений с проверкой прав доступа;</li>
 *   <li>обновления данных объявления и его изображения.</li>
 * </ul>
 *
 * <p>Основные особенности реализации:
 * <ul>
 *   <li>использует репозитории {@link AdsRepository} и {@link UsersRepository} для доступа к данным;</li>
 *   <li>применяет маппер {@link AdMapper} для преобразования между сущностями и DTO;</li>
 *   <li>сохраняет изображения объявлений в файловую систему (путь задаётся через свойство {@code app.images.dir});</li>
 *   <li>генерирует уникальные имена файлов для изображений с использованием времени и случайного числа;</li>
 *   <li>проверяет права доступа: только автор или администратор может удалять/обновлять объявление.</li>
 * </ul>
 *
 * @see AdsService
 * @see AdsRepository
 * @see UsersRepository
 * @see AdMapper
 */
@Service
public class AdsServiceImpl implements AdsService {

    /**
     * Репозиторий для работы с сущностями объявлений {@link AdEntity}.
     */
    private final AdsRepository adsRepository;

    /**
     * Маппер для преобразования между {@link AdEntity} и DTO ({@link Ad}, {@link ExtendedAd}).
     */
    private final AdMapper adMapper;

    /**
     * Репозиторий для работы с сущностями пользователей {@link UserEntity}.
     */
    private final UsersRepository usersRepository;

    /**
     * Генератор случайных чисел для создания уникальных имён файлов изображений.
     */
    private final Random rnd = new Random();

    /**
     * Путь к директории для хранения изображений объявлений.
     *
     * <p>Задаётся через свойство конфигурации {@code app.images.dir}.
     */
    @Value("${app.images.dir}")
    private String adsImagePath;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param adsRepository репозиторий объявлений
     * @param adMapper маппер для преобразования сущностей и DTO
     * @param usersRepository репозиторий пользователей
     */
    public AdsServiceImpl(AdsRepository adsRepository, AdMapper adMapper, UsersRepository usersRepository) {
        this.adsRepository = adsRepository;
        this.adMapper = adMapper;
        this.usersRepository = usersRepository;
    }

    /**
     * Получает список всех объявлений в системе.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Загружает все сущности {@link AdEntity} из репозитория.</li>
     *   <li>Преобразует их в DTO {@link Ad} с помощью {@link AdMapper#toDto}.</li>
     *   <li>Формирует объект {@link Ads} с общим количеством и списком объявлений.</li>
     * </ol>
     *
     * @return объект {@link Ads}, содержащий все объявления системы
     * @see Ads
     * @see Ad
     */
    @Override
    public Ads getAllAds() {
        List<Ad> ads = adsRepository.findAll().stream()
                .map(adMapper::toDto)
                .toList();
        Ads result = new Ads();
        result.setCount(ads.size());
        result.setResults(ads);
        return result;
    }

    /**
     * Создаёт новое объявление с указанным изображением.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Находит пользователя по email (параметр {@code name}).</li>
     *   <li>Создаёт директорию для изображений, если её нет.</li>
     *   <li>Генерирует уникальное имя файла на основе времени и случайного числа.</li>
     *   <li>Копирует файл изображения в целевую директорию.</li>
     *   <li>Заполняет сущность {@link AdEntity} данными из DTO и пути к изображению.</li>
     *   <li>Сохраняет сущность в БД и возвращает DTO {@link Ad}.</li>
     * </ol>
     *
     * @param name email пользователя, создающего объявление
     * @param properties DTO {@link CreateOrUpdateAd} с данными объявления
     * @param image файл изображения в формате {@link MultipartFile}
     * @return объект {@link Ad} с данными созданного объявления
     * @throws IOException если ошибка при работе с файловой системой
     * @see CreateOrUpdateAd
     * @see MultipartFile
     */
    @Override
    public Ad addAd(String name, CreateOrUpdateAd properties, MultipartFile image) throws IOException {
        AdEntity ad = new AdEntity();
        Path adsImageDirectory = Paths.get(adsImagePath);
        UserEntity user = usersRepository.findByEmail(name);

        Files.createDirectories(adsImageDirectory);
        String extension = getFileExtension(image.getOriginalFilename());
        String fileName = System.currentTimeMillis() + rnd.nextInt(1000) + extension;
        Path filePath = adsImageDirectory.resolve(fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        ad.setImage("/images/" + fileName);
        ad.setAdAuthor(user);
        ad.setPrice(properties.getPrice());
        ad.setTitle(properties.getTitle());
        ad.setDescription(properties.getDescription());
        adsRepository.save(ad);
        return adMapper.toDto(ad);
    }

    /**
     * Получает детальную информацию об объявлении по ID.
     *
     * <p>Если объявление не найдено, возвращает {@code null}.
     * Иначе заполняет {@link ExtendedAd} данными из {@link AdEntity} и связанной {@link UserEntity}.
     *
     * @param id идентификатор объявления
     * @return объект {@link ExtendedAd} или {@code null}, если объявление не существует
     * @see ExtendedAd
     */
    @Override
    public ExtendedAd getAds(int id) {
        AdEntity ad = adsRepository.findById(id).orElse(null);
        if (ad == null) {
            return null;
        }
        ExtendedAd extendedAd = new ExtendedAd();
        extendedAd.setPk(ad.getId());
        extendedAd.setAuthorFirstName(ad.getAdAuthor().getFirstName());
        extendedAd.setAuthorLastName(ad.getAdAuthor().getLastName());
        extendedAd.setDescription(ad.getDescription());
        extendedAd.setEmail(ad.getAdAuthor().getEmail());
        extendedAd.setImage(ad.getImage());
        extendedAd.setPhone(ad.getAdAuthor().getPhone());
        extendedAd.setPrice(ad.getPrice());
        extendedAd.setTitle(ad.getTitle());
        return extendedAd;
    }

    /**
     * Удаляет объявление по ID с проверкой прав доступа.
     *
     * <p>Возвращает код результата:
     * <ul>
     *   <li>0 — успешно удалено;</li>
     *   <li>1 — объявление не найдено;</li>
     *   <li>2 — у пользователя нет прав на удаление (не автор и не администратор).</li>
     * </ul>
     *
     * @param name email пользователя, пытающегося удалить объявление
     * @param id идентификатор объявления
     * @return код результата операции (0, 1 или 2)
     */
    @Override
    public int removeAd(String name, int id) throws IOException {
        AdEntity ad = adsRepository.findById(id).orElse(null);
        if (ad == null) {
            return 1;
        }
        UserEntity user = usersRepository.findByEmail(name);
        if (!user.getRole().equals("ADMIN") && !ad.getAdAuthor().getEmail().equals(name)) {
            return 2;
        }

        Path path = Paths.get(adsImagePath);
        String fileName = ad.getImage().substring(ad.getImage().lastIndexOf('/') + 1);
        Path filePath = path.resolve(fileName);
        Files.deleteIfExists(filePath);
        adsRepository.deleteById(id);
        return 0;
    }

    /**
     * Обновляет информацию об объявлении (заголовок, цену, описание) с проверкой прав доступа.
     *
     * <p>Если объявление не найдено, возвращает {@code null}.
     * Если у пользователя нет прав (не автор и не администратор), выбрасывает {@link SecurityException}.
     *
     * @param name email пользователя, пытающегося обновить объявление
     * @param properties DTO {@link CreateOrUpdateAd} с новыми данными объявления
     * @param id идентификатор объявления
     * @return объект {@link Ad} с обновлёнными данными или {@code null}, если объявление не найдено
     * @throws SecurityException если у пользователя нет прав на обновление
     * @see CreateOrUpdateAd
     * @see Ad
     */
    @Override
    public Ad updateAds(String name, CreateOrUpdateAd properties, int id) throws SecurityException {
        AdEntity ad = adsRepository.findById(id).orElse(null);
        if (ad == null) {
            return null;
        }
        UserEntity user = usersRepository.findByEmail(name);
        if (!user.getRole().equals("ADMIN") && !ad.getAdAuthor().getEmail().equals(name)) {
            throw new SecurityException();
        }

        ad.setTitle(properties.getTitle());
        ad.setPrice(properties.getPrice());
        ad.setDescription(properties.getDescription());
        adsRepository.save(ad);
        return adMapper.toDto(ad);
    }

    /**
     * Получает список объявлений конкретного пользователя.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Находит пользователя по email.</li>
     *   <li>Получает список его объявлений из связанной сущности.</li>
     *   <li>Преобразует каждую сущность {@link AdEntity} в DTO {@link Ad}.</li>
     *   <li>Формирует объект {@link Ads} с количеством и списком объявлений.</li>
     * </ol>
     *
     * @param name email пользователя
     * @return объект {@link Ads}, содержащий объявления пользователя
     * @see Ads
     * @see Ad
     */
    @Override
    public Ads getAdsMe(String name) {
        UserEntity user = usersRepository.findByEmail(name);
        List<Ad> ads = user.getAds().stream()
                .map(adMapper::toDto)
                .toList();
        Ads result = new Ads();
        result.setCount(ads.size());
        result.setResults(ads);
        return result;
    }

    /**
     * Обновляет изображение объявления с проверкой прав доступа.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Проверяет существование объявления и права пользователя.</li>
     *   <li>Удаляет старое изображение, если оно есть.</li>
     *   <li>Генерирует новое уникальное имя файла.</li>
     *   <li>Копирует новый файл изображения в целевую директорию.</li>
     *   <li>Обновляет путь к изображению в сущности {@link AdEntity}.</li>
     *   <li>Сохраняет обновленную сущность в БД.</li>
     *   <li>Возвращает байтовый массив нового изображения.</li>
     * </ol>
     *
     * @param name email пользователя, пытающегося обновить изображение
     * @param id идентификатор объявления
     * @param image новый файл изображения в формате {@link MultipartFile}
     * @return байтовый массив с данными нового изображения или {@code null}, если объявление не найдено
     * @throws IOException если ошибка при работе с файловой системой
     * @throws SecurityException если у пользователя нет прав на обновление
     * @see MultipartFile
     */
    @Override
    public byte[] updateImage(String name, int id, MultipartFile image) throws IOException {
        AdEntity ad = adsRepository.findById(id).orElse(null);
        if (ad == null) {
            return null;
        }
        UserEntity user = usersRepository.findByEmail(name);
        if (!user.getRole().equals("ADMIN") && !ad.getAdAuthor().getEmail().equals(name)) {
            throw new SecurityException();
        }
        Path adsImageDirectory = Paths.get(adsImagePath);
        Files.createDirectories(adsImageDirectory);
        String oldFileName = ad.getImage().substring(ad.getImage().lastIndexOf('/') + 1);
        Path oldFilePath = adsImageDirectory.resolve(oldFileName);
        Files.deleteIfExists(oldFilePath);
        String extension = getFileExtension(image.getOriginalFilename());
        String newFileName = System.currentTimeMillis() + rnd.nextInt(1000) + extension;
        Path newFilePath = adsImageDirectory.resolve(newFileName);
        Files.copy(image.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);
        ad.setImage("images/" + newFileName);
        adsRepository.save(ad);
        return Files.readAllBytes(newFilePath);
    }

    /**
     * Извлекает расширение файла из его имени.
     *
     * <p>Пример: для имени {@code "photo.jpg"} вернёт {@code ".jpg"}.
     *
     * @param filename исходное имя файла
     * @return расширение файла (подстрока после последней точки)
     */
    public String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.'));
    }

}
