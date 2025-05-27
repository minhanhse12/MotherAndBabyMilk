package com.motherandbabymilk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Registration {

    @Schema(description = "User's username (should be a valid email)", example = "user@example.com")
    private String username;

    @Schema(description = "User's password", example = "password123")
    private String password;

    @Schema(description = "User's role", example = "admin, staff, customer")
    private String role;

    @Schema(description = "User's full name", example = "Nguyen Van A")
    private String fullName;

    @Schema(description = "User's address", example = "Tan Phu, Ho Chi Minh")
    private String address;

    @Schema(description = "User's phone number", example = "0912345678")
    private String phone;
}