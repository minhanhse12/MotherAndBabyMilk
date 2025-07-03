package com.motherandbabymilk.dto.response;

import com.motherandbabymilk.entity.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private int id;
    private int userId;
    private LocalDateTime orderDate;
    private LocalDateTime paidAt;
    private double totalAmount;
    private String address;
    private OrderStatus status;
    private List<OrderItemResponse> orderItems;
}
