package com.motherandbabymilk.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private int userId;
    private double totalAmount;
    private List<OrderItemRequest> orderItems;
}
