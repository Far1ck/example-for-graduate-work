package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;

@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdvertisementsController {

    @GetMapping
    public ResponseEntity<Ads> getAds() {
        return ResponseEntity.ok(new Ads());
    }

    @PostMapping
    public ResponseEntity<Ad> createAd(@RequestParam("properties") CreateOrUpdateAd properties,
                                       @RequestParam("image") MultipartFile image) {
        if (false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new Ad());
    }
}
