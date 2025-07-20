package com.motherandbabymilk.service;

import com.motherandbabymilk.dto.request.CartRequest;
import com.motherandbabymilk.dto.response.CartItemResponse;
import com.motherandbabymilk.dto.response.CartResponse;
import com.motherandbabymilk.dto.response.CartOperationResponse;
import com.motherandbabymilk.entity.Cart;
import com.motherandbabymilk.entity.CartItem;
import com.motherandbabymilk.entity.Product;
import com.motherandbabymilk.entity.Users;
import com.motherandbabymilk.exception.EntityNotFoundException;
import com.motherandbabymilk.repository.CartItemRepository;
import com.motherandbabymilk.repository.CartRepository;
import com.motherandbabymilk.repository.ProductRepository;
import com.motherandbabymilk.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public CartOperationResponse addToCart(int userId, @Valid CartRequest request) {
        Users user = userRepository.findUsersById(userId);
        if (user == null) {
            return new CartOperationResponse(false, "User with ID " + userId + " not found");
        }
        Product product = productRepository.findById(request.getProductId())
                .orElse(null);
        if (product == null) {
            return new CartOperationResponse(false, "Product with ID " + request.getProductId() + " not found");
        }
        if (product.getQuantity() < request.getQuantity()) {
            return new CartOperationResponse(false, "Insufficient stock for product " + product.getName() + ". Available: " + product.getQuantity());
        }

        Cart cart = cartRepository.findByUserIdAndStatus(userId, "active")
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setStatus("active");
                    newCart.setCreatedAt(LocalDateTime.now());
                    newCart.setTotalPrice(0.0);
                    return cartRepository.save(newCart);
                });

        CartItem cartItem = cartItemRepository.findByCartIdAndProductIdAndIsDeleteFalse(cart.getId(), product.getId())
                .orElse(new CartItem());
        int newQuantity = cartItem.getQuantity() + request.getQuantity();
        if (product.getQuantity() < newQuantity) {
            return new CartOperationResponse(false, "Insufficient stock for product " + product.getName() + ". Available: " + product.getQuantity());
        }

        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(newQuantity);
        cartItem.setUnitPrice(product.getPrice());
        cartItem.setTotalPrice(newQuantity * product.getPrice());
        cartItem.setNote(request.getNote());
        cartItem.setAddedAt(LocalDateTime.now());
        cartItem.setSelect(true);
        cartItemRepository.save(cartItem);

        updateCartTotalPrice(cart);
        logger.info("Added product ID {} to cart ID {}", product.getId(), cart.getId());
        return new CartOperationResponse(true, "Product " + product.getName() + " added to cart successfully", getCart(userId));
    }

    @Transactional
    public CartOperationResponse updateCartItem(int userId, int productId, @Min(value = 1, message = "Quantity must be at least 1") int quantity) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, "active")
                .orElse(null);
        if (cart == null) {
            return new CartOperationResponse(false, "Cart not found for user ID " + userId);
        }
        CartItem cartItem = cartItemRepository.findByCartIdAndProductIdAndIsDeleteFalse(cart.getId(), productId)
                .orElse(null);
        if (cartItem == null) {
            return new CartOperationResponse(false, "Cart item not found for product ID " + productId);
        }
        Product product = productRepository.findById(productId)
                .orElse(null);
        if (product == null) {
            return new CartOperationResponse(false, "Product with ID " + productId + " not found");
        }
        if (product.getQuantity() < quantity) {
            return new CartOperationResponse(false, "Insufficient stock for product " + product.getName() + ". Available: " + product.getQuantity());
        }

        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(quantity * cartItem.getUnitPrice());
        cartItem.setAddedAt(LocalDateTime.now());
        cartItemRepository.save(cartItem);

        updateCartTotalPrice(cart);
        logger.info("Updated cart item for product ID {} in cart ID {}", productId, cart.getId());
        return new CartOperationResponse(true, "Cart item updated successfully", getCart(userId));
    }

    @Transactional
    public void removeFromCart(int userId, int productId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, "active")
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user ID " + userId));
        CartItem cartItem = cartItemRepository.findByCartIdAndProductIdAndIsDeleteFalse(cart.getId(), productId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found for product ID " + productId));

        cartItem.setDelete(true);
        cartItemRepository.save(cartItem);

        updateCartTotalPrice(cart);
        logger.info("Removed product ID {} from cart ID {}", productId, cart.getId());
    }

    public CartResponse getCart(int userId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, "active")
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user ID " + userId));

        CartResponse response = new CartResponse();
        response.setUserId(userId);
        response.setCartItems(cartItemRepository.findByCartIdAndIsDeleteFalse(cart.getId()).stream()
                .map(item -> modelMapper.map(item, CartItemResponse.class))
                .collect(Collectors.toList()));
        response.setTotalPrice(cart.getTotalPrice());
        response.setStatus(cart.getStatus());

        return response;
    }

    @Transactional
    public void clearCart(int userId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, "active")
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user ID " + userId));
        cartItemRepository.findByCartIdAndIsDeleteFalse(cart.getId())
                .forEach(item -> {
                    item.setDelete(true);
                    cartItemRepository.save(item);
                });
        cart.setTotalPrice(0.0);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        logger.info("Cleared cart ID {} for user ID {}", cart.getId(), userId);
    }

    private void updateCartTotalPrice(Cart cart) {
        double totalPrice = cartItemRepository.findByCartIdAndIsDeleteFalse(cart.getId()).stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
        cart.setTotalPrice(totalPrice);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }
}