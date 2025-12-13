package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentsRepository;
import ru.skypro.homework.repository.UsersRepository;
import ru.skypro.homework.service.CommentsService;

import java.util.List;

@Service
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;
    private final AdsRepository adsRepository;
    private final UsersRepository usersRepository;
    private final CommentMapper mapper;

    public CommentsServiceImpl(CommentsRepository commentsRepository, AdsRepository adsRepository, UsersRepository usersRepository, CommentMapper mapper) {
        this.commentsRepository = commentsRepository;
        this.adsRepository = adsRepository;
        this.usersRepository = usersRepository;
        this.mapper = mapper;
    }

    public Comments getComments(int id) {
        AdEntity ad = adsRepository.findById(id).orElse(null);
        if (ad == null) {
            return null;
        }
        List<Comment> comments = ad.getComments().stream()
                .map(mapper::toDto)
                .toList();
        Comments result = new Comments();
        result.setCount(comments.size());
        result.setResults(comments);
        return result;
    }
}
