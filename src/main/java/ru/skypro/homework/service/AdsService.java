package ru.skypro.homework.service;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;

import java.io.IOException;

public interface AdsService {
    Ads getAllAds();
    Ad addAd(String name, @Valid CreateOrUpdateAd properties, MultipartFile image) throws IOException;
}
