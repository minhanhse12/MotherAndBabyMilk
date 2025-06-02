package com.motherandbabymilk.dto;


import com.motherandbabymilk.entity.Roles;
import lombok.Data;

@Data
public class UpdateProfile {
    private String fullName;
    private Roles roles;
    private String phone;
    private String address;
    private boolean status;
}
