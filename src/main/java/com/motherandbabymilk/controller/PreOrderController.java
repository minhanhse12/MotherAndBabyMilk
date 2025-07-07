package com.motherandbabymilk.controller;

import com.motherandbabymilk.dto.request.PreOrderRequest;
import com.motherandbabymilk.dto.response.PreOrderResponse;
import com.motherandbabymilk.entity.PreOrderStatus;
import com.motherandbabymilk.entity.Users;
import com.motherandbabymilk.service.PreOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/preorders")
@SecurityRequirement(name = "api")
public class PreOrderController {

    @Autowired
    private PreOrderService preOrderService;

    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Create a pre-order")
    public ResponseEntity<PreOrderResponse> createPreOrder(
            @AuthenticationPrincipal Users currentUser,
            @Valid @RequestBody PreOrderRequest request) {

        return ResponseEntity.ok(preOrderService.createPreOrder(currentUser.getId(), request));
    }

    @PutMapping("/{preOrderId}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @Operation(summary = "Update pre-order status")
    public ResponseEntity<PreOrderResponse> updatePreOrderStatus(
            @Parameter(description = "ID of the pre-order", example = "123")
            @PathVariable int preOrderId,

            @Parameter(description = "New status", example = "CONFIRMED")
            @RequestParam PreOrderStatus status) {
        return ResponseEntity.ok(preOrderService.updatePreOrderStatus(preOrderId, status));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Get current user's pre-orders")
    public ResponseEntity<List<PreOrderResponse>> getMyPreOrders(@AuthenticationPrincipal Users currentUser) {
        return ResponseEntity.ok(preOrderService.getUserPreOrders(currentUser.getId()));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @Operation(summary = "Get pre-orders by status")
    public ResponseEntity<List<PreOrderResponse>> getPreOrdersByStatus(
            @Parameter(description = "Pre-order status", example = "PENDING")
            @PathVariable PreOrderStatus status) {
        return ResponseEntity.ok(preOrderService.getAllPreOrdersByStatus(status));
    }
}
