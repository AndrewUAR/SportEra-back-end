package com.sportera.sportera.services;

import com.sportera.sportera.models.PasswordResetToken;
import com.sportera.sportera.models.User;
import com.sportera.sportera.repositories.PasswordResetTokenRepository;
import com.sportera.sportera.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class AuthUserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User is not found"));
        validateLoginAttempt(user);
        userRepository.save(user);
        return UserDetailsImpl.build(user);
    }

    private void validateLoginAttempt(User user) {
        if (user.isNotLocked()) {
            if(loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByResetToken(token).orElseThrow(() -> new RuntimeException("Invalid reset token"));
        return !isTokenFound(passwordResetToken) ? "invalidToken"
                : isTokenExpired(passwordResetToken) ? "expired"
                : null;
    }

    private boolean isTokenFound(PasswordResetToken passwordToken) {
        return passwordToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passwordToken) {
        final Calendar calendar = Calendar.getInstance();
        return passwordToken.getExpiryDate().before(calendar.getTime());
    }


}
