package com.sportera.sportera.services;

import com.sportera.sportera.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("emailSenderService")
public class EmailSenderService {

    @Autowired
    private JavaMailSender javaMailSender;

    private SimpleMailMessage constructEmail(String subject, String body, User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom("no-repply@sportera.com");
        return email;
    }

   public SimpleMailMessage constructResetTokenEmail(String token, User user) {
        String url = "http://localhost:8080/api/1.0/auth/change-password?token=" + token;
        String message = "Your password reset link: ";
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

   public SimpleMailMessage constructConfirmationTokenEmail(String confirmationToken, User user) {
        String url = "http://localhost:8080/api/1.0/auth/confirm-account?token=" + confirmationToken;
        String message = "To confirm your account, please click here: ";
        return constructEmail("Complete Registration!", message + " \r\n" + url, user);
    }

    public void sendEmail(SimpleMailMessage email) {
        this.javaMailSender.send(email);
    }
}
