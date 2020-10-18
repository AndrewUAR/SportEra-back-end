package com.sportera.sportera.payloads.response;

import lombok.Data;

import java.util.List;

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
}
