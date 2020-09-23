package com.sportera.sportera.payloads.request;

import com.sportera.sportera.helpers.UniqueEmail;
import com.sportera.sportera.helpers.UniqueUsername;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class SignupRequest {

    @NotNull(message = "{sportera.constraints.username.NotNull.message}")
    @Size(min = 4, max = 25)
    @UniqueUsername
    private String username;

    @NotNull(message = "{sportera.constraints.email.NotNull.message}")
    @Size(max = 255)
    @Email
    @UniqueEmail
    private String email;

    @NotNull(message = "{sportera.constraints.password.NotNull.message}")
    @Size(min = 8, max = 255)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message="{sportera.constraints.password.Pattern.message}")
    private String password;

}
