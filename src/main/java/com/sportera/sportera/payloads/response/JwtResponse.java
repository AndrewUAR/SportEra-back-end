package com.sportera.sportera.payloads.response;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long id;
    private String username;
    private String email;
    private Boolean isActive;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String username, String email, Boolean isActive, List<String> roles) {
        this.accessToken = accessToken;
        this.id = id;
        this.username = username;
        this.isActive = isActive;
        this.email = email;
        this.roles = roles;
    }
}
