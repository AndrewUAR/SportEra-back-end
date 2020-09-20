package com.sportera.sportera.controllers;

import com.sportera.sportera.errors.ApiError;
import com.sportera.sportera.models.ConfirmationToken;
import com.sportera.sportera.models.User;
import com.sportera.sportera.payloads.response.JwtResponse;
import com.sportera.sportera.repositories.ConfirmationTokenRepository;
import com.sportera.sportera.repositories.UserRepository;
import com.sportera.sportera.security.jwt.JwtUtils;
import com.sportera.sportera.services.EmailSenderService;
import com.sportera.sportera.services.UserDetailsImpl;
import com.sportera.sportera.services.UserService;
import com.sportera.sportera.shared.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
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

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {

        userService.save(user);
        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        System.out.println(confirmationTokenRepository.save(confirmationToken));

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom("no-repply@sportera.com");
        mailMessage.setText("To confirm your account, please click here : "
                +"http://localhost:8082/confirm-account?token="+confirmationToken.getConfirmationToken());

        emailSenderService.sendEmail(mailMessage);

        return ResponseEntity.ok(new GenericResponse("User saved"));
    }

    @PostMapping("/signin")
    ResponseEntity<?> loginUser(@RequestBody User user) {
//        if (userRepository.existsByUsername(user.getUsername())
//                && userRepository.findByUsername(user.getUsername()).isActive() == false) {
//            System.out.println("In auth");
//            return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED);
//        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getIsActive(),
                roles));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        System.out.println(exception.getMessage());
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
