package ru.skypro.homework.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "ads")
@Data
public class AdEntity {

    private String image;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private int price;
    private String title;
    private String description;

    @OneToMany(mappedBy = "commentAd", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CommentEntity> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    private UserEntity adAuthor;
}

