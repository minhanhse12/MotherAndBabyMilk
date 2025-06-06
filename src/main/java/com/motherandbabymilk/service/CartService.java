package com.motherandbabymilk.service;

import com.motherandbabymilk.dto.request.CartRequest;
import com.motherandbabymilk.dto.response.CartItemResponse;
import com.motherandbabymilk.dto.response.CartResponse;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing cart-related operations.
 */
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

    /**
     * Adds a product to the user's cart or updates the quantity if it already exists.
     * Checks product stock before adding.
     * @param userId the ID of the user
     * @param request the cart request DTO
     * @return the updated cart response
     */
    @Transactional
    public CartResponse addToCart(int userId, @Valid CartRequest request) {
        Users user = userRepository.findUsersById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User with ID " + userId + " not found");
        }
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + request.getProductId() + " not found"));
        if (product.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock for product ID " + product.getId());
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
            throw new IllegalArgumentException("Insufficient stock for product ID " + product.getId());
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
        return getCart(userId);
    }

    /**
     * Updates the quantity of a cart item.
     * Checks product stock before updating.
     * @param userId the ID of the user
     * @param productId the ID of the product
     * @param quantity the new quantity
     * @return the updated cart response
     */
    @Transactional
    public CartResponse updateCartItem(int userId, int productId, @Min(value = 1, message = "Quantity must be at least 1") int quantity) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, "active")
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user ID " + userId));
        CartItem cartItem = cartItemRepository.findByCartIdAndProductIdAndIsDeleteFalse(cart.getId(), productId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found for product ID " + productId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + productId + " not found"));

        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for product ID " + productId);
        }

        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(quantity * cartItem.getUnitPrice());
        cartItem.setAddedAt(LocalDateTime.now());
        cartItemRepository.save(cartItem);

        updateCartTotalPrice(cart);
        logger.info("Updated cart item for product ID {} in cart ID {}", productId, cart.getId());
        return getCart(userId);
    }

    /**
     * Removes a product from the user's cart.
     * @param userId the ID of the user
     * @param productId the ID of the product
     */
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

    /**
     * Retrieves the user's cart.
     * @param userId the ID of the user
     * @return the cart response
     */
    public CartResponse getCart(int userId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, "active")
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user ID " + userId));

        CartResponse response = new CartResponse();
        response.setUserId(userId);
                response.setCartItems(cartItemRepository.findByCartIdAndIsDeleteFalse(cart.getId()).stream()
                .map(item -> {
                    CartItemResponse itemResponse = new CartItemResponse();
                    itemResponse.setId((int) item.getId());
                    itemResponse.setProductId(item.getProduct().getId());
                    itemResponse.setProductName(item.getProduct().getName());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setUnitPrice(item.getUnitPrice());
                    itemResponse.setTotalPrice(item.getTotalPrice());
                    itemResponse.setNote(item.getNote());
                    itemResponse.setSelected(item.isSelect());
                    return itemResponse;
                })
                .collect(Collectors.toList()));
        response.setTotalPrice(cart.getTotalPrice());
        response.setStatus(cart.getStatus());

        return response;
    }

    /**
     * Clears the user's cart (soft delete all items).
     * @param userId the ID of the user
     */
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

    /**
     * Updates the total price of the cart based on its items.
     * @param cart the cart to update
     */
    private void updateCartTotalPrice(Cart cart) {
        double totalPrice = cartItemRepository.findByCartIdAndIsDeleteFalse(cart.getId()).stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
        cart.setTotalPrice(totalPrice);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }
}