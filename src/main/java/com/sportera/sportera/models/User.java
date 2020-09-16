package com.sportera.sportera.models;

import com.sportera.sportera.helpers.UniqueEmail;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(	name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = "email")
      )
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "{sportera.constraints.firstName.NotNull.message}")
    @Size(min = 4, max = 25)
    @Column(name="first_name")
    private String firstName;

    @NotNull(message = "{sportera.constraints.lastName.NotNull.message}")
    @Size(min = 4, max = 25)
    @Column(name="last_name")
    private String lastName;

    @NotNull(message = "{sportera.constraints.email.NotNull.message}")
    @Size(max = 255)
    @Email
    @UniqueEmail
    @Column(name="email")
    private String email;

    @NotNull(message = "{sportera.constraints.password.NotNull.message}")
    @Size(min = 8, max = 255)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message="{sportera.constraints.password.Pattern.message}")
    @Column(name="password")
    private String password;

    @Column(name="is_active")
    private boolean isActive;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

}
