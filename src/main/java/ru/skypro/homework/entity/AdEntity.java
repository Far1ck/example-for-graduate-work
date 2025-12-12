package ru.skypro.homework.entity;

import jakarta.persistence.*;
import lombok.Data;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    private UserEntity adAuthor;
}

