package com.motherandbabymilk.dto.response;

import lombok.Data;

@Data
public class UpdateResponse {
    private int id;
    private String email;
    private String phone;
    private String address;
}
