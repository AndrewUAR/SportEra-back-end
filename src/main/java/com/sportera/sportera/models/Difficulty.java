package com.sportera.sportera.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "difficulties")
public class Difficulty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;
}
