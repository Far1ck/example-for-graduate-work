package ru.skypro.homework.entity;

import jakarta.persistence.*;
import ru.skypro.homework.dto.Role;

import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String phone;
    private Role role;
    private String image;

    @OneToMany(mappedBy = "commentAuthor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CommentEntity> comments;

    @OneToMany(mappedBy = "adAuthor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AdEntity> ads;
}
