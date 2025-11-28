package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;

@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdvertisementsController {

    @GetMapping
    public ResponseEntity<Ads> getAllAds() {
        return ResponseEntity.ok(new Ads());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Ad> addAd(@RequestParam("properties") CreateOrUpdateAd properties,
                                    @RequestParam("image") MultipartFile image) {
        if (false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new Ad());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAd> getAds(@PathVariable("id") int id) {
        if (false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (false) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ExtendedAd());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeAd(@PathVariable("id") int id) {
        if (false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (false) {
            return ResponseEntity.notFound().build();
        }
        if (false) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Ad> updateAds(@PathVariable("id") int id,
                                        @RequestBody CreateOrUpdateAd properties) {
        if (false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (false) {
            return ResponseEntity.notFound().build();
        }
        if (false) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(new Ad());
    }

    @GetMapping("/me")
    public ResponseEntity<Ads> getAdsMe() {
        if (false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(new Ads());
    }

    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> updateImage(@PathVariable("id") int id,
                                              @RequestParam("image") MultipartFile image) {
        byte[] updatedImage = new byte[1];
        if (false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (false) {
            return ResponseEntity.notFound().build();
        }
        if (false) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(updatedImage);
    }
}
