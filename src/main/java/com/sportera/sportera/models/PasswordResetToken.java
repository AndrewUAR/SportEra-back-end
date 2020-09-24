package com.sportera.sportera.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Data
@Entity
public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 20;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String resetToken;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expiryDate;

    public PasswordResetToken(User user) {
        this.user = user;
        this.expiryDate = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(EXPIRATION));
        resetToken = UUID.randomUUID().toString();
    }

}
