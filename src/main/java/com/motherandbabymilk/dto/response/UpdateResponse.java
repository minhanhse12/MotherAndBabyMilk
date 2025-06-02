package com.motherandbabymilk.dto.response;

import com.motherandbabymilk.entity.Roles;
import lombok.Data;

@Data
public class UpdateResponse {
    private String fullName;
    private Roles roles;
    private String phone;
    private String address;
    private boolean status;
}
