package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.homework.dto.UpdateUser;

@RestController
@RequiredArgsConstructor
public class UsersController {

    @PatchMapping("/me")
    public ResponseEntity<UpdateUser> updateUser (@RequestBody UpdateUser updateUser) {
        if (true) {
            return ResponseEntity.ok(new UpdateUser());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
