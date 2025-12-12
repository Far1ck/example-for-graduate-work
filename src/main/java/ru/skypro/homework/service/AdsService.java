package ru.skypro.homework.service;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;

import java.io.IOException;

public interface AdsService {
    Ads getAllAds();
    Ad addAd(String name, CreateOrUpdateAd properties, MultipartFile image) throws IOException;
    ExtendedAd getAds(int id);
    int removeAd(String name, int id);
    Ad updateAds(String name, CreateOrUpdateAd properties, int id) throws SecurityException;
    Ads getAdsMe(String name);
}
