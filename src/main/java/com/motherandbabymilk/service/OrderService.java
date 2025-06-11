package com.motherandbabymilk.service;

import com.motherandbabymilk.dto.request.OrderRequest;
import com.motherandbabymilk.dto.response.OrderResponse;
import com.motherandbabymilk.entity.*;
import com.motherandbabymilk.exception.EntityNotFoundException;
import com.motherandbabymilk.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ModelMapper modelMapper;


    @Transactional
    public OrderResponse placeOrderFromCart(int userId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, "active")
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user ID " + userId));

        List<CartItem> selectedItems = cartItemRepository.findByCartIdAndIsDeleteFalse(cart.getId())
                .stream()
                .filter(CartItem::isSelect)
                .toList();

        if (selectedItems.isEmpty()) {
            throw new IllegalStateException("No selected items in cart");
        }

        Users user = userRepository.findUsersById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (CartItem ci : selectedItems) {

            Product product = ci.getProduct();
            if (product.getQuantity() < ci.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product ID " + product.getId());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(product);
            item.setQuantity(ci.getQuantity());
            item.setTotalAmount(ci.getUnitPrice());
            orderItems.add(item);

            total += ci.getTotalPrice();
        }

        Date paymentDeadline = new Date(System.currentTimeMillis() + 5 * 1000 * 60);
        order.setPaymentDeadline(paymentDeadline);
        order.setStatus(OrderStatus.PENDING);

        order.setTotalAmount(total);
        order.setOrderItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        cart.setStatus("ordered");
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        selectedItems.forEach(item -> {
            item.setDelete(true);
            cartItemRepository.save(item);
        });

        return modelMapper.map(savedOrder, OrderResponse.class);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkAndCancelExpiredOrders() {
        cancelExpiredOrders();
    }

    @Transactional
    public void cancelExpiredOrders() {
        List<Order> expiredOrders = orderRepository.findAllByStatus(OrderStatus.PENDING)
                .stream()
                .filter(order -> order.getPaymentDeadline() != null && order.getPaymentDeadline().before(new Date()))
                .toList();

        for (Order order : expiredOrders) {
            order.setStatus(OrderStatus.CANCELED);
            orderRepository.save(order);
        }
    }

    @Transactional
    public OrderResponse getOrder(int orderId) {
        Order order = orderRepository.findByIdAndIsDeleteFalse(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return modelMapper.map(order, OrderResponse.class);
    }

    @Transactional
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findByIsDeleteFalse();
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .toList();
    }

    @Transactional
    public OrderResponse updateOrder(int orderId, OrderRequest request) {
        Order order = orderRepository.findByIdAndIsDeleteFalse(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setTotalAmount(request.getTotalAmount());

        Order updatedOrder = orderRepository.save(order);
        return modelMapper.map(updatedOrder, OrderResponse.class);
    }

    @Transactional
    public void deleteOrder(int orderId) {
        Order order = orderRepository.findByIdAndIsDeleteFalse(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setDelete(true);
        orderRepository.save(order);
    }
}
