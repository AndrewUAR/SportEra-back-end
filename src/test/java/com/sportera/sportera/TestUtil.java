package com.sportera.sportera;

import com.sportera.sportera.models.User;

public class TestUtil {

    public static User createValidUser() {
        User user = new User();
        user.setUsername("test-user");
        user.setEmail("test@gmail.com");
        user.setPassword("P4ssword");
        return user;
    }
}

