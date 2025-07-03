package com.motherandbabymilk.controller;

import com.motherandbabymilk.dto.request.PreOrderRequest;
import com.motherandbabymilk.dto.response.PreOrderResponse;
import com.motherandbabymilk.entity.PreOrderStatus;
import com.motherandbabymilk.service.PreOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/preorders")
@SecurityRequirement(name = "api")
public class PreOrderController {

    @Autowired
    private PreOrderService preOrderService;

    @PostMapping("/{userId}")
    @Operation(summary = "Create a pre-order")
    public ResponseEntity<PreOrderResponse> createPreOrder(@PathVariable int userId, @Valid @RequestBody PreOrderRequest request) {
        System.out.println("123");
        return ResponseEntity.ok(preOrderService.createPreOrder(userId, request));
    }

    @PutMapping("/{preOrderId}/status")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Update pre-order status")
    public ResponseEntity<PreOrderResponse> updatePreOrderStatus(@PathVariable int preOrderId, @RequestParam PreOrderStatus status) {
        return ResponseEntity.ok(preOrderService.updatePreOrderStatus(preOrderId, status));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PreOrderResponse>> getUserPreOrders(@PathVariable("userId") int userId) {
        return ResponseEntity.ok(preOrderService.getUserPreOrders(userId));
    }


    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    @Operation(summary = "Get pre-orders by status")
    public ResponseEntity<List<PreOrderResponse>> getPreOrdersByStatus(@PathVariable PreOrderStatus status) {
        return ResponseEntity.ok(preOrderService.getAllPreOrdersByStatus(status));
    }
}