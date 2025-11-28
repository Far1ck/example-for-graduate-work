package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Comments;

@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class CommentsController {

    @GetMapping("/{id}/comments")
    public ResponseEntity<Comments> getComments(@PathVariable("id") int id) {
        if (false) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (false) {
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new Comments());
    }
}
