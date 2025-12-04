package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.entity.CommentEntity;

@Component
public class CommentMapper {

    public Comment toDto(CommentEntity entity) {
        if (entity == null) {
            return null;
        }
        Comment dto = new Comment();
        dto.setAuthor(entity.getCommentAuthor().getId());
        dto.setAuthorImage(entity.getAuthorImage());
        dto.setAuthorFirstName(entity.getAuthorFirstName());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setPk(entity.getId());
        dto.setText(entity.getText());
        return dto;
    }

    public CommentEntity toEntity(Comment dto) {
        if (dto == null) {
            return null;
        }
        CommentEntity entity = new CommentEntity();
        entity.setAuthorImage(dto.getAuthorImage());
        entity.setAuthorFirstName(dto.getAuthorFirstName());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setId(dto.getPk());
        entity.setText(dto.getText());
        return entity;
    }
}
