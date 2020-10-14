package com.sportera.sportera.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "{sportera.constraints.activityName.NotNull.message}")
    @Column(length = 20)
    private String name;
}
