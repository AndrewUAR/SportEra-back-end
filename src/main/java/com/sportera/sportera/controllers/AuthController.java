package com.sportera.sportera.controllers;

import com.sportera.sportera.errors.ApiError;
import com.sportera.sportera.models.ConfirmationToken;
import com.sportera.sportera.models.PasswordResetToken;
import com.sportera.sportera.models.User;
import com.sportera.sportera.payloads.request.LoginRequest;
import com.sportera.sportera.payloads.request.PasswordResetRequest;
import com.sportera.sportera.payloads.request.SignupRequest;
import com.sportera.sportera.payloads.response.LoginResponse;
import com.sportera.sportera.repositories.ConfirmationTokenRepository;
import com.sportera.sportera.repositories.PasswordTokenRepository;
import com.sportera.sportera.repositories.RoleRepository;
import com.sportera.sportera.repositories.UserRepository;
import com.sportera.sportera.security.jwt.JwtUtils;
import com.sportera.sportera.services.AuthUserService;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/1.0/auth")
public class  AuthController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    AuthUserService authUserService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordTokenRepository passwordTokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(signupRequest.getPassword());
        User savedUser = userService.save(user);
        ConfirmationToken confirmationToken = new ConfirmationToken(savedUser);
        confirmationTokenRepository.save(confirmationToken);
        SimpleMailMessage confirmationTokenMessage = emailSenderService
                .constructConfirmationTokenEmail(confirmationToken.getConfirmationToken(), savedUser);
        emailSenderService.sendEmail(confirmationTokenMessage);
        return ResponseEntity.ok(new GenericResponse("User saved"));
    }


    @PostMapping("/signin")
    ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        LoginResponse res = new LoginResponse(
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);

        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setSecure(false);
        cookie.setHttpOnly(false);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok(res);
    }


    @RequestMapping(value="/confirm-account", method={RequestMethod.GET, RequestMethod.POST})
    ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid link");
        }
        User user = userRepository.findByEmailIgnoreCase(token.getUser().getEmail());
        user.setActive(true);
        userRepository.save(user);
        confirmationTokenRepository.delete(token);
        return ResponseEntity.ok(new GenericResponse("Account was successfully activated!"));
    }


    @PostMapping("/forgot-password")
    ResponseEntity<?> forgotPassword(@RequestParam("email") String userEmail) {
        User user = userRepository.findByEmailIgnoreCase(userEmail);
        if (user == null) {
            throw new UsernameNotFoundException("User with this email doesn't exist!");
        }
        PasswordResetToken passwordResetToken = new PasswordResetToken(user);
        passwordTokenRepository.save(passwordResetToken);
        SimpleMailMessage resetPasswordTokenMessage = emailSenderService
                .constructResetTokenEmail(passwordResetToken.getResetToken(), user);
        emailSenderService.sendEmail(resetPasswordTokenMessage);
        return ResponseEntity.ok(new GenericResponse("Reset link was sent to your email!"));
    }

    @PostMapping("/reset-password")
    ResponseEntity<?> resetPassword(@RequestParam("token") String token, @Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        String result = authUserService.validatePasswordResetToken(token);
        if (result != null) {
            return ResponseEntity.ok(new GenericResponse("Reset token " + result));
        }
        PasswordResetToken passwordResetToken = passwordTokenRepository.findByResetToken(token);
        User user = passwordResetToken.getUser();
        if (user == null) {
            throw new UsernameNotFoundException("User was not found!");
        }
        userService.changeUserPassword(user, passwordResetRequest.getNewPassword());
        passwordTokenRepository.delete(passwordResetToken);
        return ResponseEntity.ok(new GenericResponse("Password was successfully changed!"));
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
