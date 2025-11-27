package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.homework.dto.User;

@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class UsersController {

    @GetMapping("/me")
    public ResponseEntity<User> getUser() {
        if (true) {
            return ResponseEntity.ok(new User());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
