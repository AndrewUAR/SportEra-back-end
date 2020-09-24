package com.sportera.sportera.repositories;

import com.sportera.sportera.models.ConfirmationToken;
import com.sportera.sportera.models.PasswordResetToken;
import com.sportera.sportera.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    ConfirmationToken findByResetToken(String resetToken);
    ConfirmationToken findByUser(User user);
}
