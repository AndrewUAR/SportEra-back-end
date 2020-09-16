package com.sportera.sportera.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("emailSenderService")
public class EmailSenderService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(SimpleMailMessage email) {
        this.javaMailSender.send(email);
    }
}
