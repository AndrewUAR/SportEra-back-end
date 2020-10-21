package com.sportera.sportera.payloads.response;

import com.sportera.sportera.models.UserProfile;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserProfileResponse {

    private String firstName;
    private String lastName;
    private int age;
    private String introduction;
    private String profilePicture;
    private List<String> activities;

    public UserProfileResponse(String firstName, String lastName, int age, String introduction, String profilePicture, List<String> activities) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.introduction = introduction;
        this.profilePicture = profilePicture;
        this.activities = activities;
    }

    public static UserProfileResponse build(UserProfile userProfile) {
        List<String> activities = new ArrayList<>(userProfile.getActivities())
                .stream().map(activity -> activity.getName().toString())
                .collect(Collectors.toList());

        LocalDate now = LocalDate.now();
        LocalDate birthDate = new java.sql.Date(userProfile.getDateOfBirth().getTime()).toLocalDate();
        int age = Period.between(birthDate, now).getYears();

        return new UserProfileResponse(
                userProfile.getFirstName(),
                userProfile.getLastName(),
                age,
                userProfile.getIntroduction(),
                userProfile.getProfilePicture(),
                activities
        );
    }
}
