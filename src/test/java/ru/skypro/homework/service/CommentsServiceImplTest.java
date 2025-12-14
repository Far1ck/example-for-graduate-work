package ru.skypro.homework.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentsRepository;
import ru.skypro.homework.repository.UsersRepository;
import ru.skypro.homework.service.impl.CommentsServiceImpl;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CommentsServiceImplTest {

    @Mock
    private CommentsRepository commentsRepository;

    @Mock
    private AdsRepository adsRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private CommentMapper mapper;

    @InjectMocks
    private CommentsServiceImpl commentsService;

    private AdEntity ad;
    private UserEntity user;
    private CommentEntity comment;

    @BeforeEach
    void setUp() {
        ad = new AdEntity();

        user = new UserEntity();

        comment = new CommentEntity();
        comment.setId(100);
        comment.setText("Old comment text");
        comment.setCommentAuthor(user);
        comment.setCommentAd(ad);

        ad.setId(1);
        ad.setComments(List.of(comment));

        user.setEmail("user@example.com");
        user.setFirstName("John");
        user.setImage("image.png");
        user.setRole("USER");
    }

    @Test
    void getComments_shouldReturnComments_whenAdExists() {
        Comment commentDto = new Comment();
        when(adsRepository.findById(1)).thenReturn(Optional.of(ad));
        when(mapper.toDto(comment)).thenReturn(commentDto);

        Comments result = commentsService.getComments(1);

        assertNotNull(result);
        assertEquals(1, result.getCount());
        assertEquals(commentDto, result.getResults().get(0));

        verify(adsRepository).findById(1);
    }

    @Test
    void getComments_shouldReturnNull_whenAdNotFound() {
        when(adsRepository.findById(999)).thenReturn(Optional.empty());

        Comments result = commentsService.getComments(999);

        assertNull(result);

        verify(adsRepository).findById(999);
        verifyNoInteractions(mapper); // mapper не вызывается
    }

    @Test
    void addComment_shouldAddComment_whenAdAndUserExist() {
        CreateOrUpdateComment properties = new CreateOrUpdateComment();
        properties.setText("New comment");

        when(adsRepository.findById(1)).thenReturn(Optional.of(ad));
        when(usersRepository.findByEmail("user@example.com")).thenReturn(user);
        when(mapper.toDto(any(CommentEntity.class))).thenReturn(new Comment());

        Comment result = commentsService.addComment("user@example.com", 1, properties);

        assertNotNull(result);
        verify(commentsRepository).save(any(CommentEntity.class));
    }

    @Test
    void addComment_shouldReturnNull_whenAdNotFound() {
        CreateOrUpdateComment properties = new CreateOrUpdateComment();
        properties.setText("Text");

        when(adsRepository.findById(999)).thenReturn(Optional.empty());

        Comment result = commentsService.addComment("user@example.com", 999, properties);

        assertNull(result);
        verify(usersRepository, never()).findByEmail(any());
        verify(commentsRepository, never()).save(any());
    }

    @Test
    void deleteComment_shouldReturn0_whenUserIsAuthor() {
        when(commentsRepository.findById(100)).thenReturn(Optional.of(comment));
        when(usersRepository.findByEmail("user@example.com")).thenReturn(user);

        int result = commentsService.deleteComment("user@example.com", 1, 100);

        assertEquals(0, result);
        verify(commentsRepository).deleteById(100);
    }

    @Test
    void deleteComment_shouldReturn1_whenCommentNotFound() {
        when(commentsRepository.findById(999)).thenReturn(Optional.empty());

        int result = commentsService.deleteComment("user@example.com", 1, 999);

        assertEquals(1, result);
        verify(usersRepository, never()).findByEmail(any());
        verify(commentsRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteComment_shouldReturn2_whenUserNotAuthorOrAdmin() {
        UserEntity otherUser = new UserEntity();
        otherUser.setEmail("other@example.com");
        otherUser.setRole("USER");

        when(commentsRepository.findById(100)).thenReturn(Optional.of(comment));
        when(usersRepository.findByEmail("other@example.com")).thenReturn(otherUser);

        int result = commentsService.deleteComment("other@example.com", 1, 100);

        assertEquals(2, result);
        verify(commentsRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteComment_shouldReturn0_whenUserIsAdmin() {
        user.setRole("ADMIN");

        when(commentsRepository.findById(100)).thenReturn(Optional.of(comment));
        when(usersRepository.findByEmail("admin@example.com")).thenReturn(user);

        int result = commentsService.deleteComment("admin@example.com", 1, 100);

        assertEquals(0, result);
        verify(commentsRepository).deleteById(100);
    }

    @Test
    void updateComment_shouldUpdateText_whenUserIsAuthor() throws Exception {
        CreateOrUpdateComment properties = new CreateOrUpdateComment();
        properties.setText("Updated text");

        when(commentsRepository.findById(100)).thenReturn(Optional.of(comment));
        when(usersRepository.findByEmail("user@example.com")).thenReturn(user);
        when(commentsRepository.save(comment)).thenReturn(comment);
        when(mapper.toDto(comment)).thenReturn(new Comment());

        Comment result = commentsService.updateComment("user@example.com", properties, 100, 1);

        assertNotNull(result);
        assertEquals("Updated text", comment.getText());
        verify(commentsRepository).save(comment);
    }

    @Test
    void updateComment_shouldThrowSecurityException_whenUserNotAuthorOrAdmin() throws Exception {
        UserEntity otherUser = new UserEntity();
        otherUser.setEmail("other@example.com");
        otherUser.setRole("USER");

        CreateOrUpdateComment properties = new CreateOrUpdateComment();
        properties.setText("New text");

        when(commentsRepository.findById(100)).thenReturn(Optional.of(comment));
        when(usersRepository.findByEmail("other@example.com")).thenReturn(otherUser);

        assertThrows(SecurityException.class, () -> {
            commentsService.updateComment("other@example.com", properties, 100, 1);
        });

        verify(commentsRepository, never()).save(any());
    }

    @Test
    void updateComment_shouldReturnNull_whenCommentNotFound() throws Exception {
        CreateOrUpdateComment properties = new CreateOrUpdateComment();
        properties.setText("Updated text");

        when(commentsRepository.findById(999)).thenReturn(Optional.empty());

        Comment result = commentsService.updateComment("user@example.com", properties, 999, 1);

        assertNull(result);
        verify(usersRepository, never()).findByEmail(any());
        verify(commentsRepository, never()).save(any());
    }

    @Test
    void updateComment_shouldUpdateText_whenUserIsAdmin() throws Exception {
        user.setRole("ADMIN");

        CreateOrUpdateComment properties = new CreateOrUpdateComment();
        properties.setText("Updated by admin");

        when(commentsRepository.findById(100)).thenReturn(Optional.of(comment));
        when(usersRepository.findByEmail("admin@example.com")).thenReturn(user);
        when(commentsRepository.save(comment)).thenReturn(comment);
        when(mapper.toDto(comment)).thenReturn(new Comment());

        Comment result = commentsService.updateComment("admin@example.com", properties, 100, 1);

        assertNotNull(result);
        assertEquals("Updated by admin", comment.getText());
        verify(commentsRepository).save(comment);
    }
}
