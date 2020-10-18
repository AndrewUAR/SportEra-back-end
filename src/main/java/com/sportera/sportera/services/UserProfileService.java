package com.sportera.sportera.services;

import com.sportera.sportera.models.UserProfile;
import com.sportera.sportera.repositories.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserProfileService {

    @Autowired
    UserProfileRepository userProfileRepository;

    public UserProfile save(UserProfile profile) {
        return userProfileRepository.save(profile);
    }

    public List<String> getActivitiesToStringList(UserProfile userProfile) {
        return new ArrayList<>(userProfile.getActivities())
                .stream().map(activity -> activity.getName().toString())
                .collect(Collectors.toList());
    }

    public int calculateUserAge(Date dateOfBirth) {
        LocalDate now = LocalDate.now();
        LocalDate birth = new java.sql.Date(dateOfBirth.getTime()).toLocalDate();
        Period p = Period.between(birth, now);
        return p.getYears();
    }
}
