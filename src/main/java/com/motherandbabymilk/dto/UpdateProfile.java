package com.motherandbabymilk.dto;


import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateProfile {
    private String fullName;
    @Pattern(regexp = "(84|0[3|5|7|8|9])+(\\d{8})", message = "Invalid phone!")
    private String phone;
    @Pattern(regexp = "^\\d+(\\/\\d+)?\\s+[a-zA-Z\\s]+$", message = "Address format is invalid")
    private String address;
}
