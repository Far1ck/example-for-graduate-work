package ru.skypro.homework.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ads")
public class AdEntity {

    private String image;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int price;
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    private UserEntity adAuthor;
}

