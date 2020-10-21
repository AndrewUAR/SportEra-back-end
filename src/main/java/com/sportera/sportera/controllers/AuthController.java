package com.sportera.sportera.controllers;

import com.sportera.sportera.models.ConfirmationToken;
import com.sportera.sportera.models.PasswordResetToken;
import com.sportera.sportera.models.User;
import com.sportera.sportera.payloads.request.LoginRequest;
import com.sportera.sportera.payloads.request.PasswordResetRequest;
import com.sportera.sportera.payloads.request.SignupRequest;
import com.sportera.sportera.payloads.response.LoginResponse;
import com.sportera.sportera.security.jwt.JwtUtils;
import com.sportera.sportera.services.*;
import com.sportera.sportera.shared.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@RestController
@RequestMapping("/api/1.0/auth")
public class  AuthController {

    @Autowired
    UserService userService;

    @Autowired
    ConfirmationTokenService confirmationTokenService;

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    AuthUserService authUserService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordResetTokenService passwordResetTokenService;

    @Autowired
    JwtUtils jwtUtils;


    @PostMapping("/signup")
    ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        User savedUser = userService.registerUser(signupRequest);
        ConfirmationToken token = confirmationTokenService.save(savedUser);
        emailSenderService.constructSendConfirmationTokenEmail(token.getConfirmationToken(), savedUser);

        return ResponseEntity.ok(new GenericResponse("Verification email was sent."));
    }

    @PostMapping("/signin")
    ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        LoginResponse res = LoginResponse.build(userDetails);

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
        ConfirmationToken token = confirmationTokenService.findByConfirmationToken(confirmationToken);
        User user = userService.findUserByEmail(token.getUser().getEmail());
        user.setActive(true);
        userService.save(user);
        confirmationTokenService.delete(token.getId());
        return ResponseEntity.ok(new GenericResponse("Account was successfully activated!"));
    }

    @PostMapping("/forgot-password")
    ResponseEntity<?> forgotPassword(@RequestParam("email") String userEmail) {
        User user = userService.findUserByEmail(userEmail);
        PasswordResetToken passwordResetToken = passwordResetTokenService.save(user);
        emailSenderService.constructSendResetTokenEmail(passwordResetToken.getResetToken(), user);
        return ResponseEntity.ok(new GenericResponse("Reset link was sent to your email!"));
    }

    @PostMapping("/reset-password")
    ResponseEntity<?> resetPassword(@RequestParam("token") String token, @Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        String result = authUserService.validatePasswordResetToken(token);
        if (result != null) {
            return ResponseEntity.ok(new GenericResponse("Reset token " + result));
        }
        PasswordResetToken passwordResetToken = passwordResetTokenService.findByResetToken(token);
        User user = passwordResetToken.getUser();
        if (user == null) {
            throw new UsernameNotFoundException("User was not found!");
        }
        userService.changeUserPassword(user, passwordResetRequest.getNewPassword());
        passwordResetTokenService.delete(passwordResetToken.getId());
        return ResponseEntity.ok(new GenericResponse("Password was successfully changed!"));
    }

}
