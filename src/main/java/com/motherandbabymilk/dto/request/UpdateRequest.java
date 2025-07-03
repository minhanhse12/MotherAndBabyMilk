package com.motherandbabymilk.dto.request;


import com.motherandbabymilk.entity.Roles;
import lombok.Data;

@Data
public class UpdateRequest {
    private String fullName;
    private Roles roles;
    private String phone;
    private String address;
    private boolean status;
}
