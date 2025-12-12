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

@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdvertisementsController {

    private final AdsService adsService;

    @GetMapping
    public ResponseEntity<Ads> getAllAds() {
        return ResponseEntity.ok(adsService.getAllAds());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Добавление объявления",
            tags = {"Объявления"},
            operationId = "addAd"
    )
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<Ad> addAd(@RequestParam("properties") @Valid CreateOrUpdateAd properties,
                                    @RequestParam("image") MultipartFile image,
                                    Authentication authentication) throws IOException {
        Ad ad = adsService.addAd(authentication.getName(), properties, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(ad);
    }

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
    @Operation(
            summary = "Получение объявлений авторизованного пользователя",
            tags = {"Объявления"},
            operationId = "getAdsMe"
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<Ads> getAdsMe() {
        if (false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(new Ads());
    }

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
