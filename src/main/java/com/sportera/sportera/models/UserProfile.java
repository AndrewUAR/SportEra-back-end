package com.sportera.sportera.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private long id;

    @NotNull(message = "{sportera.constraints.firstName.NotNull.message}")
    @Column(name = "first_name")
    @Size(min = 2, max = 25)
    private String firstName;

    @NotNull(message = "{sportera.constraints.lastName.NotNull.message}")
    @Column(name = "last_name")
    @Size(min = 2, max = 25)
    private String lastName;

    @NotNull(message = "{sportera.constraints.dateOfBirth.NotNull.message}")
    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @NotNull(message = "{sportera.constraints.profilePicture.NotNull.message}")
    @Column(name = "profile_picture")
    private String profilePicture;

    @NotNull(message = "{sportera.constraints.introduction.NotNull.message}")
    @Column(name = "introduction")
    @Size(max = 255)
    private String introduction;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_profile_activities",
            joinColumns = @JoinColumn(name = "user_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id"))
    private Set<Activity> activities = new HashSet<>();
}
