package com.sportera.sportera.controllers;

import com.sportera.sportera.models.Activity;
import com.sportera.sportera.models.EActivity;
import com.sportera.sportera.models.User;
import com.sportera.sportera.models.UserProfile;
import com.sportera.sportera.payloads.request.CreateUserProfileRequest;
import com.sportera.sportera.payloads.response.UserProfileResponse;
import com.sportera.sportera.repositories.ActivityRepository;
import com.sportera.sportera.repositories.UserProfileRepository;
import com.sportera.sportera.repositories.UserRepository;
import com.sportera.sportera.services.UserDetailsImpl;
import com.sportera.sportera.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/1.0/user-profiles")
public class UserProfileController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    UserProfileRepository userProfileRepository;

    @GetMapping("/me")
    ResponseEntity<?> getUserProfile(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByUsername(userPrincipal.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserProfile userProfile = userProfileRepository.findByUser(user).orElseThrow(() -> new RuntimeException("User profile is not found"));
        List<String> userActivities = userProfileService.getActivitiesToStringList(userProfile);

        UserProfileResponse response = new UserProfileResponse(
                userProfile.getFirstName(),
                userProfile.getLastName(),
                userProfileService.calculateUserAge(userProfile.getDateOfBirth()),
                userProfile.getIntroduction(),
                userProfile.getProfilePicture(),
                userActivities);
        return ResponseEntity.ok(response);
    }


    @PostMapping
    ResponseEntity<?> createUserProfile(@RequestBody CreateUserProfileRequest createUserProfileRequest, Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);
        userProfile.setFirstName(createUserProfileRequest.getFirstName());
        userProfile.setLastName(createUserProfileRequest.getLastName());
        userProfile.setDateOfBirth(createUserProfileRequest.getDateOfBirth());
        userProfile.setProfilePicture(createUserProfileRequest.getProfilePicture());
        userProfile.setIntroduction(createUserProfileRequest.getIntroduction());
        Set<String> strActivities = createUserProfileRequest.getActivities();

        Set<Activity> activities = new HashSet<>();

        for (String strActivity : strActivities) {
            try {
                EActivity eActivity = EActivity.valueOf(strActivity.toUpperCase());
                activityRepository.findByName(eActivity).ifPresent(activities::add);
            } catch(IllegalArgumentException ignored) {}
        }

        userProfile.setActivities(activities);
        UserProfile userProfileSaved = userProfileService.save(userProfile);

        List<String> userActivities = userProfileService.getActivitiesToStringList(userProfileSaved);

        UserProfileResponse response = new UserProfileResponse(
                userProfileSaved.getFirstName(),
                userProfileSaved.getLastName(),
                userProfileService.calculateUserAge(userProfileSaved.getDateOfBirth()),
                userProfileSaved.getIntroduction(),
                userProfileSaved.getProfilePicture(),
                userActivities);

        return ResponseEntity.ok(response);
    }

}
