package com.sportera.sportera;

import com.sportera.sportera.models.ConfirmationToken;
import com.sportera.sportera.models.User;
import org.springframework.mail.SimpleMailMessage;

public class TestUtil {

    public static User createValidUser() {
        User user = new User();
        user.setUsername("test-user");
        user.setEmail("test@gmail.com");
        user.setPassword("P4ssword");
        return user;
    }

    public static ConfirmationToken createConfirmationToken() {
        ConfirmationToken confirmationToken = new ConfirmationToken(createValidUser());
        return confirmationToken;
    }

    public static User createValidUser2() {
        User user = new User();
        user.setUsername("test-user2");
        user.setEmail("test2@gmail.com");
        user.setPassword("P4ssword");
        return user;
    }

    public static User createValidUser3() {
        User user = new User();
        user.setUsername("test-user3");
        user.setEmail("test3@gmail.com");
        user.setPassword("P4ssword");
        user.setActive(true);
        return user;
    }

    public static User createLoginUser() {
        User user = new User();
        user.setUsername("test-user3");
        user.setPassword("P4ssword");
        return user;
    }

    public static SimpleMailMessage createMailMessage(String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("recipient@mailsac.com");
        mailMessage.setText(message);
        mailMessage.setSubject("Testing");
        mailMessage.setFrom("mysporteraua@gmail.com");
        return mailMessage;
    }

}

