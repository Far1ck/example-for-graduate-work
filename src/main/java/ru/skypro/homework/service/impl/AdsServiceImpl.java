package ru.skypro.homework.service.impl;

import jakarta.validation.Valid;
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

@Service
public class AdsServiceImpl implements AdsService {

    private final AdsRepository adsRepository;
    private final AdMapper adMapper;
    private final UsersRepository usersRepository;
    private final Random rnd = new Random();

    @Value("${app.images.dir}")
    private String adsImagePath;

    public AdsServiceImpl(AdsRepository adsRepository, AdMapper adMapper, UsersRepository usersRepository) {
        this.adsRepository = adsRepository;
        this.adMapper = adMapper;
        this.usersRepository = usersRepository;
    }

    public Ads getAllAds() {
        List<Ad> ads = adsRepository.findAll().stream()
                .map(adMapper::toDto)
                .toList();
        Ads result = new Ads();
        result.setCount(ads.size());
        result.setResults(ads);
        return result;
    }

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

    public int removeAd(String name, int id) {
        AdEntity ad = adsRepository.findById(id).orElse(null);
        if (ad == null) {
            return 1;
        }
        UserEntity user = usersRepository.findByEmail(name);
        if (!user.getRole().equals("ADMIN") && !ad.getAdAuthor().getEmail().equals(name)) {
            return 2;
        }

        adsRepository.deleteById(id);
        return 0;
    }

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

    public byte[] updateImage(String name, int id, MultipartFile image) throws IOException {
        Path adsImageDirectory = Paths.get(adsImagePath);
        AdEntity ad = adsRepository.findById(id).orElse(null);
        if (ad == null) {
            return null;
        }
        UserEntity user = usersRepository.findByEmail(name);
        if (!user.getRole().equals("ADMIN") && !ad.getAdAuthor().getEmail().equals(name)) {
            throw new SecurityException();
        }
        Files.createDirectories(adsImageDirectory);
        String oldFileName = ad.getImage().substring(ad.getImage().lastIndexOf('/') + 1);
        Path oldFilePath = adsImageDirectory.resolve(oldFileName);
        Files.deleteIfExists(oldFilePath);
        String extension = getFileExtension(image.getOriginalFilename());
        String newFileName = System.currentTimeMillis() + rnd.nextInt(1000) + extension;
        Path newFilePath = adsImageDirectory.resolve(newFileName);
        Files.copy(image.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);
        ad.setImage("images/" + newFileName);
        return Files.readAllBytes(newFilePath);
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.'));
    }

}
