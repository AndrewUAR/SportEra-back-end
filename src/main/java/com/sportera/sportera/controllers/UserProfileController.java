package com.sportera.sportera.controllers;

import com.sportera.sportera.models.User;
import com.sportera.sportera.models.UserProfile;
import com.sportera.sportera.payloads.request.UserProfileRequest;
import com.sportera.sportera.payloads.response.UserProfileResponse;
import com.sportera.sportera.repositories.ActivityRepository;
import com.sportera.sportera.repositories.UserProfileRepository;
import com.sportera.sportera.repositories.UserRepository;
import com.sportera.sportera.services.UserDetailsImpl;
import com.sportera.sportera.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/1.0")
public class UserProfileController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    UserProfileRepository userProfileRepository;

    @GetMapping("/user-profiles")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> getUserProfiles() {
        List<UserProfileResponse> userProfiles = userProfileService.getUserProfiles();
        if (!userProfiles.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(userProfiles);
    }

    @GetMapping("/user-profiles/{id}")
    ResponseEntity<?> getUserProfile(@PathVariable("id") Long id) {
        UserProfileResponse response = userProfileService.getUserProfile(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-profiles/me")
    ResponseEntity<?> getMyProfile(Authentication authentication) {
        UserProfileResponse response = userProfileService.getMyProfile(authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user-profiles")
    ResponseEntity<?> createUserProfile(@Valid @RequestBody UserProfileRequest userProfileRequest, Authentication authentication) {
        UserProfileResponse response = userProfileService.save(userProfileRequest, authentication);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/user-profiles/me")
    ResponseEntity<?> updateMyProfile(@Valid @RequestBody UserProfileRequest userProfileRequest, Authentication authentication) {
       
    }

}
