package com.sportera.sportera.payloads.response;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {

    private Long id;
    private String username;
    private String email;
    private List<String> roles;

    public LoginResponse(Long id, String username, String email, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

}
