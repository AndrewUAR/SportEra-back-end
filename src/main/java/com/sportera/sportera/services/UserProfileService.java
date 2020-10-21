package com.sportera.sportera.services;

import com.sportera.sportera.models.Activity;
import com.sportera.sportera.models.EActivity;
import com.sportera.sportera.models.User;
import com.sportera.sportera.models.UserProfile;
import com.sportera.sportera.payloads.request.UserProfileRequest;
import com.sportera.sportera.payloads.response.UserProfileResponse;
import com.sportera.sportera.repositories.ActivityRepository;
import com.sportera.sportera.repositories.UserProfileRepository;
import com.sportera.sportera.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserProfileService {

    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivityRepository activityRepository;

    public List<UserProfileResponse> getUserProfiles() {
        List<UserProfileResponse> userProfiles = new ArrayList<UserProfileResponse>();
        userProfileRepository.findAll().forEach(profile -> {
            UserProfileResponse userProfile = UserProfileResponse.build(profile);
            userProfiles.add(userProfile);
        });
        return userProfiles;
    }

    public UserProfileResponse getUserProfile(Long id) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        return UserProfileResponse.build(userProfile);
    }

    public UserProfileResponse getMyProfile(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        return UserProfileResponse.build(userProfile);
    }

    public UserProfileResponse save(UserProfileRequest userProfileRequest, Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);
        userProfile.setFirstName(userProfileRequest.getFirstName());
        userProfile.setLastName(userProfileRequest.getLastName());
        userProfile.setDateOfBirth(userProfileRequest.getDateOfBirth());
        userProfile.setProfilePicture(userProfileRequest.getProfilePicture());
        userProfile.setIntroduction(userProfileRequest.getIntroduction());
        Set<String> strActivities = userProfileRequest.getActivities();

        Set<Activity> activities = new HashSet<>();

        for (String strActivity : strActivities) {
            try {
                EActivity eActivity = EActivity.valueOf(strActivity.toUpperCase());
                activityRepository.findByName(eActivity).ifPresent(activities::add);
            } catch(IllegalArgumentException ignored) {}
        }

        userProfile.setActivities(activities);
        UserProfile userProfileSaved = userProfileRepository.save(userProfile);
        return UserProfileResponse.build(userProfileSaved);
    }

    public UserProfileResponse updateMe() {

    }
}
