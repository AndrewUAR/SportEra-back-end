package com.sportera.sportera.models;

import com.sportera.sportera.helpers.UniqueEmail;
import com.sportera.sportera.helpers.UniqueUsername;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(	name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        }
      )
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private long id;

    @Column(name="string_id")
    private String stringId;

    @NotNull(message = "{sportera.constraints.username.NotNull.message}")
    @Size(min = 4, max = 25)
    @UniqueUsername
    @Column(name="username")
    private String username;

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

    @Column(nullable = false, name="is_active")
    private boolean isActive = true;

    @Column(nullable = false, name="is_not_locked")
    private boolean isNotLocked = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {
        this.stringId = UUID.randomUUID().toString();
    }

}
