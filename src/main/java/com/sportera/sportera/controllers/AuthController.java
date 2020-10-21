package com.sportera.sportera.controllers;

import com.sportera.sportera.models.ConfirmationToken;
import com.sportera.sportera.models.PasswordResetToken;
import com.sportera.sportera.models.User;
import com.sportera.sportera.payloads.request.LoginRequest;
import com.sportera.sportera.payloads.request.PasswordResetRequest;
import com.sportera.sportera.payloads.request.SignupRequest;
import com.sportera.sportera.payloads.response.LoginResponse;
import com.sportera.sportera.repositories.ConfirmationTokenRepository;
import com.sportera.sportera.repositories.PasswordResetTokenRepository;
import com.sportera.sportera.repositories.RoleRepository;
import com.sportera.sportera.repositories.UserRepository;
import com.sportera.sportera.security.jwt.JwtUtils;
import com.sportera.sportera.services.*;
import com.sportera.sportera.shared.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
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
    ConfirmationTokenService confirmationTokenService;

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    AuthUserService authUserService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    PasswordResetTokenService passwordResetTokenService;

    @Autowired
    JwtUtils jwtUtils;


    @PostMapping("/signup")
    ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        User savedUser = userService.registerUser(signupRequest);
        ConfirmationToken token = confirmationTokenService.save(savedUser);
        emailSenderService.constructConfirmationTokenEmail(token.getConfirmationToken(), savedUser);

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
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken).orElseThrow(() -> new RuntimeException("Invalid link"));
        User user = userRepository.findByEmailIgnoreCase(token.getUser().getEmail()).orElseThrow(() -> new RuntimeException("User doesn't exist"));
        user.setActive(true);
        userRepository.save(user);
        confirmationTokenService.delete(token);
        return ResponseEntity.ok(new GenericResponse("Account was successfully activated!"));
    }

    @PostMapping("/forgot-password")
    ResponseEntity<?> forgotPassword(@RequestParam("email") String userEmail) {
        User user = userRepository.findByEmailIgnoreCase(userEmail).orElseThrow(() -> new UsernameNotFoundException("User with this email doesn't exist!"));
        PasswordResetToken passwordResetToken = new PasswordResetToken(user);
        passwordResetTokenService.save(passwordResetToken);
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
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByResetToken(token).orElseThrow(() -> new RuntimeException("Invalid reset token"));
        User user = passwordResetToken.getUser();
        if (user == null) {
            throw new UsernameNotFoundException("User was not found!");
        }
        userService.changeUserPassword(user, passwordResetRequest.getNewPassword());
        passwordResetTokenService.delete(passwordResetToken);
        return ResponseEntity.ok(new GenericResponse("Password was successfully changed!"));
    }

}
