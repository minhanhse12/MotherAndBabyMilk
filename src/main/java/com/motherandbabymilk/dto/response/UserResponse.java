package com.motherandbabymilk.dto.response;

import com.motherandbabymilk.entity.Roles;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserResponse {
    private int id;
    @Schema(description = "User's email address", example = "username")
    private String username;
    @Schema(description = "User's role", example = "role")
    private Roles roles;
    @Schema(description = "User's fullName", example = "user's fullName")
    private String fullName;
    @Schema(description = "User's phone", example = "phoneNumber")
    private String phone;
    @Schema(description = "User's address", example = "address")
    private String address;
    @Schema(description = "User's token", example = "token")
    String token;
}

