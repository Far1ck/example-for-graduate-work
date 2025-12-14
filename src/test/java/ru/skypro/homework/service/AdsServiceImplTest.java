package ru.skypro.homework.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.UsersRepository;
import ru.skypro.homework.service.impl.AdsServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdsServiceImplTest {

    @Mock
    private AdsRepository adsRepository;

    @Mock
    private AdMapper adMapper;

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private AdsServiceImpl adsService;

    private UserEntity user;
    private AdEntity ad;
    private CreateOrUpdateAd createOrUpdateAd;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setEmail("user@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhone("+79991234567");
        user.setRole("USER");

        ad = new AdEntity();
        ad.setId(1);
        ad.setAdAuthor(user);
        ad.setTitle("Test Ad");
        ad.setPrice(1000);
        ad.setDescription("Test description");
        ad.setImage("/images/test.jpg");

        createOrUpdateAd = new CreateOrUpdateAd();
        createOrUpdateAd.setTitle("Updated Title");
        createOrUpdateAd.setPrice(2000);
        createOrUpdateAd.setDescription("Updated description");

        mockFile = new MockMultipartFile(
                "image",
                "photo.png",
                "image/png",
                "fake image data".getBytes()
        );
    }

    @Test
    void getAllAds_shouldReturnAllAds() {
        List<AdEntity> adsEntities = List.of(ad);
        List<Ad> adsDtos = List.of(new Ad());

        when(adsRepository.findAll()).thenReturn(adsEntities);
        when(adMapper.toDto(ad)).thenReturn(adsDtos.get(0));


        Ads result = adsService.getAllAds();

        assertEquals(1, result.getCount());
        assertEquals(adsDtos, result.getResults());
        verify(adsRepository).findAll();
    }

    @Test
    void addAd_shouldCreateAdWithImage() throws IOException {
        when(usersRepository.findByEmail("user@example.com")).thenReturn(user);

        ReflectionTestUtils.setField(
                adsService,
                "adsImagePath",  // имя поля
                "tmp/images"     // тестовое значение
        );

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            mockedFiles.when(() -> Files.createDirectories(any()))
                    .thenAnswer(invocation -> null);
            mockedFiles.when(() -> Files.copy((Path) any(), any(), any()))
                    .thenAnswer(invocation -> null);
            mockedFiles.when(() -> Files.deleteIfExists(any()))
                    .thenAnswer(invocation -> null);

            when(adMapper.toDto(any(AdEntity.class))).thenReturn(new Ad());


            Ad result = adsService.addAd("user@example.com", createOrUpdateAd, mockFile);

            assertNotNull(result);
            verify(adsRepository).save(any(AdEntity.class));
        }
    }

    @Test
    void getAds_shouldReturnExtendedAd_whenAdExists() {
        when(adsRepository.findById(1)).thenReturn(Optional.of(ad));


        ExtendedAd result = adsService.getAds(1);

        assertNotNull(result);
        assertEquals(1, result.getPk());
        assertEquals("John", result.getAuthorFirstName());
        assertEquals("Doe", result.getAuthorLastName());
        assertEquals("user@example.com", result.getEmail());
        assertEquals("+79991234567", result.getPhone());
        assertEquals("/images/test.jpg", result.getImage());
        assertEquals(1000, result.getPrice());
        assertEquals("Test Ad", result.getTitle());
        assertEquals("Test description", result.getDescription());
    }

    @Test
    void getAds_shouldReturnNull_whenAdNotFound() {
        when(adsRepository.findById(999)).thenReturn(Optional.empty());

        ExtendedAd result = adsService.getAds(999);

        assertNull(result);
    }

    @Test
    void removeAd_shouldReturn0_whenAdDeleted() {
        when(adsRepository.findById(1)).thenReturn(Optional.of(ad));
        when(usersRepository.findByEmail("user@example.com")).thenReturn(user);

        int result = adsService.removeAd("user@example.com", 1);

        assertEquals(0, result);
        verify(adsRepository).deleteById(1);
    }

    @Test
    void removeAd_shouldReturn1_whenAdNotFound() {
        when(adsRepository.findById(999)).thenReturn(Optional.empty());

        int result = adsService.removeAd("user@example.com", 999);

        assertEquals(1, result);
    }

    @Test
    void removeAd_shouldReturn2_whenUserHasNoRights() {
        UserEntity otherUser = new UserEntity();
        otherUser.setEmail("other@example.com");
        otherUser.setRole("USER");

        when(adsRepository.findById(1)).thenReturn(Optional.of(ad));
        when(usersRepository.findByEmail("other@example.com")).thenReturn(otherUser);

        int result = adsService.removeAd("other@example.com", 1);

        assertEquals(2, result);
    }

    @Test
    void updateAds_shouldUpdateAd_whenUserIsAuthor() throws SecurityException {
        when(adsRepository.findById(1)).thenReturn(Optional.of(ad));
        when(usersRepository.findByEmail("user@example.com")).thenReturn(user);
        when(adMapper.toDto(ad)).thenReturn(new Ad());

        Ad result = adsService.updateAds("user@example.com", createOrUpdateAd, 1);

        assertNotNull(result);
        assertEquals("Updated Title", ad.getTitle());
        assertEquals(2000, ad.getPrice());
        assertEquals("Updated description", ad.getDescription());
        verify(adsRepository).save(ad);
    }

    @Test
    void updateAds_shouldThrowSecurityException_whenUserNotAuthorOrAdmin() {
        UserEntity otherUser = new UserEntity();
        otherUser.setEmail("other@example.com");
        otherUser.setRole("USER");

        when(adsRepository.findById(1)).thenReturn(Optional.of(ad));
        when(usersRepository.findByEmail("other@example.com")).thenReturn(otherUser);

        assertThrows(SecurityException.class, () ->
                adsService.updateAds("other@example.com", createOrUpdateAd, 1)
        );
    }

    @Test
    void updateAds_shouldReturnNull_whenAdNotFound() {
        when(adsRepository.findById(999)).thenReturn(Optional.empty());

        Ad result = adsService.updateAds("user@example.com", createOrUpdateAd, 999);

        assertNull(result);
        verify(adsRepository, never()).save(any());
    }

    @Test
    void getAdsMe_shouldReturnUserAds() {
        user.setAds(List.of(ad));
        List<Ad> adsDtos = List.of(new Ad());

        when(usersRepository.findByEmail("user@example.com")).thenReturn(user);
        when(adMapper.toDto(ad)).thenReturn(adsDtos.get(0));

        Ads result = adsService.getAdsMe("user@example.com");

        assertEquals(1, result.getCount());
        assertEquals(adsDtos, result.getResults());
        verify(usersRepository).findByEmail("user@example.com");
    }

    @Test
    void getAdsMe_shouldReturnEmptyAds_whenUserHasNoAds() {
        user.setAds(List.of());

        when(usersRepository.findByEmail("user@example.com")).thenReturn(user);

        Ads result = adsService.getAdsMe("user@example.com");

        assertEquals(0, result.getCount());
        assertTrue(result.getResults().isEmpty());
    }

    @Test
    void updateImage_shouldUpdateImage_whenUserIsAuthor() throws IOException {
        byte[] bytes = mockFile.getBytes();
        when(adsRepository.findById(1)).thenReturn(Optional.of(ad));
        when(usersRepository.findByEmail("user@example.com")).thenReturn(user);

        ReflectionTestUtils.setField(
                adsService,
                "adsImagePath",  // имя поля
                "tmp/images"     // тестовое значение
        );

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            mockedFiles.when(() -> Files.createDirectories(any()))
                    .thenAnswer(invocation -> null);
            mockedFiles.when(() -> Files.copy((Path) any(), any(), any()))
                    .thenAnswer(invocation -> null);
            mockedFiles.when(() -> Files.deleteIfExists(any()))
                    .thenAnswer(invocation -> null);
            mockedFiles.when(() -> Files.readAllBytes(any()))
                    .thenAnswer(invocation -> bytes);

            byte[] result = adsService.updateImage("user@example.com", 1, mockFile);

            assertNotNull(result);
            assertNotEquals("test.jpg", ad.getImage());
            assertTrue(ad.getImage().contains(".png"));
            verify(adsRepository).save(ad);
        }
    }

    @Test
    void updateImage_shouldThrowSecurityException_whenUserNotAuthorOrAdmin() {
        UserEntity otherUser = new UserEntity();
        otherUser.setEmail("other@example.com");
        otherUser.setRole("USER");

        when(adsRepository.findById(1)).thenReturn(Optional.of(ad));
        when(usersRepository.findByEmail("other@example.com")).thenReturn(otherUser);

        assertThrows(SecurityException.class, () ->
                adsService.updateImage("other@example.com", 1, mockFile)
        );
    }

    @Test
    void updateImage_shouldReturnNull_whenAdNotFound() throws IOException {
        when(adsRepository.findById(999)).thenReturn(Optional.empty());

        byte[] result = adsService.updateImage("user@example.com", 999, mockFile);

        assertNull(result);
    }

    @Test
    void getFileExtension_shouldReturnExtension() {
        String extension = adsService.getFileExtension("image.jpg");
        assertEquals(".jpg", extension);
    }

    @Test
    void getFileExtension_shouldHandleMultipleDots() {
        String extension = adsService.getFileExtension("archive.tar.gz");
        assertEquals(".gz", extension);
    }

    @Test
    void getFileExtension_shouldThrowIllegalArgumentException_whenNoExtension() {
        assertThrows(StringIndexOutOfBoundsException.class, () -> {
            adsService.getFileExtension("filename");
        });
    }

    @Test
    void getFileExtension_shouldThrowIllegalArgumentException_whenEmptyFilename() {
        assertThrows(StringIndexOutOfBoundsException.class, () -> {
            adsService.getFileExtension("");
        });
    }

    @Test
    void getFileExtension_shouldThrowIllegalArgumentException_whenNullFilename() {
        assertThrows(NullPointerException.class, () -> {
            adsService.getFileExtension(null);
        });
    }
}
