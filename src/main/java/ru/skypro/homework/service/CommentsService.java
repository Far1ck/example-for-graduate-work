package ru.skypro.homework.service;

import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;

public interface CommentsService {
    Comments getComments(int id);
    Comment addComment(String name, int id, CreateOrUpdateComment properties);
}
