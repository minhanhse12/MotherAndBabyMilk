package com.motherandbabymilk.controller;

import com.motherandbabymilk.dto.request.CartRequest;
import com.motherandbabymilk.dto.response.CartResponse;
import com.motherandbabymilk.entity.Users;
import com.motherandbabymilk.exception.EntityNotFoundException;
import com.motherandbabymilk.service.CartService;
import com.motherandbabymilk.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/carts")
@Tag(name = "Cart Management", description = "APIs for managing user shopping carts")
@SecurityRequirement(name = "api")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;


    @PostMapping
    @Operation(summary = "Add product to cart", description = "Adds a product to the current user's cart")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody CartRequest request) {
        Users user = userService.getCurrentAccount();
        CartResponse response = cartService.addToCart(user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update cart item quantity", description = "Updates the quantity of a product in the current user's cart")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable int productId,
            @RequestParam @Min(value = 1, message = "Quantity must be at least 1") int quantity) {
        Users user = userService.getCurrentAccount();
        CartResponse response = cartService.updateCartItem(user.getId(), productId, quantity);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove product from cart", description = "Removes a product from the current user's cart")
    public ResponseEntity<Void> removeFromCart(@PathVariable int productId) {
        Users user = userService.getCurrentAccount();
        cartService.removeFromCart(user.getId(), productId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    @Operation(summary = "Get cart", description = "Retrieves the current user's cart")
    public ResponseEntity<CartResponse> getCart() {
        Users user = userService.getCurrentAccount();
        CartResponse response = cartService.getCart(user.getId());
        return ResponseEntity.ok(response);
    }


    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Clears all items from the current user's cart")
    public ResponseEntity<Void> clearCart() {
        Users user = userService.getCurrentAccount();
        cartService.clearCart(user.getId());
        return ResponseEntity.noContent().build();
    }
}