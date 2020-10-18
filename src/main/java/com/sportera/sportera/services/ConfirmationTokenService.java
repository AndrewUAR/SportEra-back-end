package com.sportera.sportera.services;

import com.sportera.sportera.models.ConfirmationToken;
import com.sportera.sportera.repositories.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfirmationTokenService {

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationToken save(ConfirmationToken token) {
        return confirmationTokenRepository.save(token);
    }

    public void delete(ConfirmationToken token) {
        confirmationTokenRepository.delete(token);
    }
}
