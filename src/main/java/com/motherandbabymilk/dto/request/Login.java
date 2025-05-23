package com.motherandbabymilk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class Login {
    @Schema(description = "User's email address", example = "user@example.com")
    @Email(message = "Email not valid!")
    private String username;
    @Schema(description = "User's password", example = "password123")
    @NotBlank(message = "Password can not blank!")
    private String password;

}
