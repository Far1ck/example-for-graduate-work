package ru.skypro.homework.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class CommentEntity {

    private String authorImage;
    private String authorFirstName;
    private long createdAt;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    private UserEntity commentAuthor;
}
