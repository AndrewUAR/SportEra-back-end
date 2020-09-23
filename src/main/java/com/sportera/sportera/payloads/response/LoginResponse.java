package com.sportera.sportera.payloads.response;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {

    private Long id;
    private String username;
    private String email;
    private Boolean isActive;
    private List<String> roles;

    public LoginResponse(Long id, String username, String email, Boolean isActive, List<String> roles) {
        this.id = id;
        this.username = username;
        this.isActive = isActive;
        this.email = email;
        this.roles = roles;
    }

}
