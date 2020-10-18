package com.sportera.sportera.repositories;

import com.sportera.sportera.models.PasswordResetToken;
import com.sportera.sportera.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByResetToken(String resetToken);
    Optional<PasswordResetToken> findByUser(User user);
}
