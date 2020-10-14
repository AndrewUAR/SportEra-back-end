package com.sportera.sportera.payloads.response;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {

    private String username;
    private String email;
    private List<String> roles;

    public LoginResponse(String username, String email, List<String> roles) {
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

}
