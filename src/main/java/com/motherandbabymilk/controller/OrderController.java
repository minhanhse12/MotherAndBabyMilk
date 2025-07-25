package com.motherandbabymilk.controller;

import com.motherandbabymilk.dto.request.OrderRequest;
import com.motherandbabymilk.dto.response.OrderResponse;
import com.motherandbabymilk.entity.OrderStatus;
import com.motherandbabymilk.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@SecurityRequirement(name = "api")
public class OrderController {

    @Autowired
    private OrderService orderService;

//    @PostMapping("/place/{userId}")
//    public ResponseEntity<OrderResponse> placeOrderFromCart(
//            @PathVariable int userId,
//            @RequestParam String address) {
//
//        OrderResponse response = orderService.placeOrderFromCart(userId, address);
//        return ResponseEntity.ok(response);
//    }
    @PostMapping("/place/{userId}")
    public ResponseEntity<OrderResponse> placeOrderFromCart(
            @PathVariable int userId,
            @RequestParam String address,
            @RequestParam(defaultValue = "false") boolean usePoints) {

        OrderResponse response = orderService.placeOrderFromCart(userId, address, usePoints);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable int orderId) {
        OrderResponse response = orderService.getOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getOrderbyUserId/{userId}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ADMIN', 'STAFF')")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable int userId) {
        try {
            List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting user orders: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> responseList = orderService.getAllOrders();
        return ResponseEntity.ok(responseList);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable int orderId,
            @RequestBody OrderRequest request) {
        OrderResponse response = orderService.updateOrder(orderId, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable int orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("Order has been deleted.");
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable int orderId,
            @RequestParam OrderStatus status) {

        OrderResponse response = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(response);
    }
}


