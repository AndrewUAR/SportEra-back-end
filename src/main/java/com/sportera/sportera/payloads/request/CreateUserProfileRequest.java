package com.sportera.sportera.payloads.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Data
public class CreateUserProfileRequest {

    @NotNull(message = "{sportera.constraints.firstName.NotNull.message}")
    @Size(min = 2, max = 25)
    private String firstName;

    @NotNull(message = "{sportera.constraints.lastName.NotNull.message}")
    @Size(min = 2, max = 25)
    private String lastName;

    @NotNull(message = "{sportera.constraints.dateOfBirth.NotNull.message}")
    private Date dateOfBirth;

    @NotNull(message = "{sportera.constraints.profilePicture.NotNull.message}")
    private String profilePicture;

    @NotNull(message = "{sportera.constraints.introduction.NotNull.message}")
    @Size(max = 255)
    private String introduction;

    @NotEmpty
    private Set<String> activities;

}
