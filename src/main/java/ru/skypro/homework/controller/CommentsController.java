package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;

@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class CommentsController {

    @GetMapping("/{id}/comments")
    @Operation(
            summary = "Получение комментариев объявления",
            tags = {"Комментарии"},
            operationId = "getComments"
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<Comments> getComments(@PathVariable("id") int id) {
        if (false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (false) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new Comments());
    }

    @PostMapping("/{id}/comments")
    @Operation(
            summary = "Добавление комментария к объявлению",
            tags = {"Комментарии"},
            operationId = "addComment"
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<Comment> addComment(@PathVariable("id") int id,
                                              @RequestBody CreateOrUpdateComment properties) {
        if (false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (false) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new Comment());
    }

    @DeleteMapping("/{adId}/comments/{commentId}")
    @Operation(
            summary = "Удаление комментария",
            tags = {"Комментарии"},
            operationId = "deleteComment"
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<?> deleteComment(@PathVariable("adId") int adId,
                                           @PathVariable("commentId") int commentId) {
        if (false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (false) {
            return ResponseEntity.notFound().build();
        }
        if (false) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{adId}/comments/{commentId}")
    @Operation(
            summary = "Обновление комментария",
            tags = {"Комментарии"},
            operationId = "updateComment"
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<Comment> updateComment(@PathVariable("adId") int adId,
                                           @PathVariable("commentId") int commentId,
                                           @RequestBody CreateOrUpdateComment properties) {
        if (false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (false) {
            return ResponseEntity.notFound().build();
        }
        if (false) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(new Comment());
    }
}
