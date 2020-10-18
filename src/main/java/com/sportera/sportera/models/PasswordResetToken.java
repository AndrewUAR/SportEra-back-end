package com.sportera.sportera.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Data
@Entity
@NoArgsConstructor
@Table(name="password_reset_tokens")
public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    private String resetToken;

    private Date expiryDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public PasswordResetToken(User user) {
        this.user = user;
        this.expiryDate = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(EXPIRATION));
        resetToken = UUID.randomUUID().toString();
    }

}
