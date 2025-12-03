package ru.skypro.homework.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class CommentEntity {

    @Column(name = "author_image")
    private String authorImage;
    @Column(name = "author_first_name")
    private String authorFirstName;
    @Column(name = "created_at")
    private long createdAt;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    private UserEntity commentAuthor;
}
