package com.sportera.sportera.controllers;

import com.sportera.sportera.error.ApiError;
import com.sportera.sportera.models.ConfirmationToken;
import com.sportera.sportera.models.User;
import com.sportera.sportera.repositories.ConfirmationTokenRepository;
import com.sportera.sportera.repositories.UserRepository;
import com.sportera.sportera.services.EmailSenderService;
import com.sportera.sportera.services.UserService;
import com.sportera.sportera.shared.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/1.0")
public class  AuthController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {

//        if (userRepository.existsByUsername(user.getUsername())) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new GenericResponse("Error: Username is already taken!"));
//        }
//
//        if (userRepository.existsByEmail(user.getEmail())) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new GenericResponse("Error: Email is already in use!"));
//        }

        userService.save(user);
        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        System.out.println(confirmationTokenRepository.save(confirmationToken));

//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(user.getEmail());
//        mailMessage.setSubject("Complete Registration!");
//        mailMessage.setFrom("no-repply@sportera.com");
//        mailMessage.setText("To confirm your account, please click here : "
//                +"http://localhost:8082/confirm-account?token="+confirmationToken.getConfirmationToken());
//
//        emailSenderService.sendEmail(mailMessage);

        return ResponseEntity.ok(new GenericResponse("User saved"));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        ApiError apiError = new ApiError(400, "Validation error", request.getServletPath());
        BindingResult result = exception.getBindingResult();
        Map<String, String> validationErrors = new HashMap<>();
        for(FieldError fieldError: result.getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        apiError.setValidationErrors(validationErrors);
        return apiError;
    }
}
