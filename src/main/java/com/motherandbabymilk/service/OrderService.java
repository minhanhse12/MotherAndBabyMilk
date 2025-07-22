package com.motherandbabymilk.service;

import com.motherandbabymilk.dto.request.OrderRequest;
import com.motherandbabymilk.dto.response.OrderItemResponse;
import com.motherandbabymilk.dto.response.OrderResponse;
import com.motherandbabymilk.entity.*;
import com.motherandbabymilk.exception.EntityNotFoundException;
import com.motherandbabymilk.exception.IllegalStateException;
import com.motherandbabymilk.repository.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


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

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

//    @Transactional
//    public OrderResponse placeOrderFromCart(int userId, String address) {
//
//        if (address == null || address.trim().isEmpty()) {
//            throw new IllegalArgumentException("Address is required");
//        }
//
//        Users user = userRepository.findUsersById(userId);
//        if (user == null) {
//            throw new EntityNotFoundException("User not found");
//        }
//
//        Cart cart = cartRepository.findByUserId(userId)
//                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user ID " + userId));
//
//        List<CartItem> selectedItems = cartItemRepository.findByCartIdAndIsDeleteFalse(cart.getId())
//                .stream()
//                .filter(CartItem::isSelect)
//                .toList();
//
//        if (selectedItems.isEmpty()) {
//            throw new IllegalStateException("No selected items in cart");
//        }
//
//        if ("pending_order".equals(cart.getStatus())) {
//            throw new IllegalStateException("Cart is already being processed for another order");
//        }
//
//        Order order = new Order();
//        order.setUser(user);
//        order.setOrderDate(LocalDateTime.now());
//        order.setAddress(address.trim());
//
//        List<OrderItem> orderItems = new ArrayList<>();
//        double total = 0;
//
//        for (CartItem ci : selectedItems) {
//            Product product = ci.getProduct();
//            if (product.getQuantity() < ci.getQuantity()) {
//                throw new IllegalArgumentException("Insufficient stock for product ID " + product.getId());
//            }
//
//            OrderItem item = new OrderItem();
//            item.setOrder(order);
//            item.setProductId(product);
//            item.setQuantity(ci.getQuantity());
//            item.setTotalAmount(ci.getUnitPrice());
//            orderItems.add(item);
//
//            total += ci.getTotalPrice();
//        }
//
//        Date paymentDeadline = new Date(System.currentTimeMillis() + 5 * 60 * 1000);
//        order.setPaymentDeadline(paymentDeadline);
//        order.setStatus(OrderStatus.PENDING);
//        order.setTotalAmount(total);
//        order.setOrderItems(orderItems);
//        Order savedOrder = orderRepository.save(order);
//
//        cart.setStatus("pending_order");
//        cart.setLastOrderId(savedOrder.getId());
//        cart.setUpdatedAt(LocalDateTime.now());
//        cartRepository.save(cart);
//
//        long delayMillis = 5 * 60 * 1000;
//        scheduler.schedule(() -> {
//            Optional<Order> optionalOrder = orderRepository.findById(savedOrder.getId());
//            if (optionalOrder.isPresent()) {
//                Order o = optionalOrder.get();
//                if (o.getStatus() == OrderStatus.PENDING &&
//                        o.getPaymentDeadline() != null &&
//                        o.getPaymentDeadline().before(new Date())) {
//
//                    o.setStatus(OrderStatus.CANCELED);
//                    orderRepository.save(o);
//
//                    Cart timeoutCart = cartRepository.findByUserId(userId).orElse(null);
//                    if (timeoutCart != null) {
//                        timeoutCart.setStatus("active");
//                        timeoutCart.setLastOrderId(0);
//                        timeoutCart.setUpdatedAt(LocalDateTime.now());
//                        cartRepository.save(timeoutCart);
//                    }
//
//                    logger.info("Order {} canceled due to payment timeout", o.getId());
//                }
//            }
//        }, delayMillis, TimeUnit.MILLISECONDS);
//
//        return convertToOrderResponse(savedOrder);
//    }
@Transactional
public OrderResponse placeOrderFromCart(int userId, String address, boolean useLoyaltyPoints) {

    if (address == null || address.trim().isEmpty()) {
        throw new IllegalArgumentException("Address is required");
    }

    Users user = userRepository.findUsersById(userId);
    if (user == null) {
        throw new EntityNotFoundException("User not found");
    }

    Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new EntityNotFoundException("Cart not found for user ID " + userId));

    List<CartItem> selectedItems = cartItemRepository.findByCartIdAndIsDeleteFalse(cart.getId())
            .stream()
            .filter(CartItem::isSelect)
            .toList();

    if (selectedItems.isEmpty()) {
        throw new IllegalStateException("No selected items in cart");
    }

    if ("pending_order".equals(cart.getStatus())) {
        throw new IllegalStateException("Cart is already being processed for another order");
    }

    Order order = new Order();
    order.setUser(user);
    order.setOrderDate(LocalDateTime.now());
    order.setAddress(address.trim());

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

    double originalTotal = total;

    int pointsUsed = 0;
    double discountAmount = 0;

    if (useLoyaltyPoints && user.getLoyaltyPoints() > 0) {
        pointsUsed = user.getLoyaltyPoints();
        double maxDiscountPercent = Math.min(pointsUsed, 100); // giới hạn 100%
        discountAmount = (maxDiscountPercent / 100.0) * total;
        total -= discountAmount;

        user.setLoyaltyPoints(user.getLoyaltyPoints() - (int) maxDiscountPercent);
        userRepository.save(user);
    }

    order.setTotalAmount(total);
    order.setStatus(OrderStatus.PENDING);
    order.setPaymentDeadline(new Date(System.currentTimeMillis() + 5 * 60 * 1000));
    order.setOrderItems(orderItems);

    Order savedOrder = orderRepository.save(order);


    cart.setStatus("pending_order");
    cart.setLastOrderId(savedOrder.getId());
    cart.setUpdatedAt(LocalDateTime.now());
    cartRepository.save(cart);

    // Schedule tự hủy nếu quá hạn
    long delayMillis = 5 * 60 * 1000;
    scheduler.schedule(() -> {
        Optional<Order> optionalOrder = orderRepository.findById(savedOrder.getId());
        if (optionalOrder.isPresent()) {
            Order o = optionalOrder.get();
            if (o.getStatus() == OrderStatus.PENDING &&
                    o.getPaymentDeadline() != null &&
                    o.getPaymentDeadline().before(new Date())) {

                o.setStatus(OrderStatus.CANCELED);
                orderRepository.save(o);

                Cart timeoutCart = cartRepository.findByUserId(userId).orElse(null);
                if (timeoutCart != null) {
                    timeoutCart.setStatus("active");
                    timeoutCart.setLastOrderId(0);
                    timeoutCart.setUpdatedAt(LocalDateTime.now());
                    cartRepository.save(timeoutCart);
                }

                logger.info("Order {} canceled due to payment timeout", o.getId());
            }
        }
    }, delayMillis, TimeUnit.MILLISECONDS);

    return convertToOrderResponse(savedOrder);
}


    @Transactional
    public OrderResponse getOrder(int orderId) {
        Order order = orderRepository.findByIdAndIsDeleteFalse(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToOrderResponse(order);
    }

    @Transactional
    public List<OrderResponse> getOrdersByUserId(int userId) {
        // Kiểm tra user có tồn tại không
        Users user = userRepository.findUsersById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }

        // Lấy danh sách orders theo userId
        List<Order> orders = orderRepository.findByUserIdAndIsDeleteFalse(userId);

        return orders.stream()
                .map(this::convertToOrderResponse)
                .toList();
    }

    @Transactional
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findByIsDeleteFalse();
        return orders.stream()
                .map(this::convertToOrderResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(int orderId, OrderStatus newStatus) {
        Order order = orderRepository.findByIdAndIsDeleteFalse(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        return convertToOrderResponse(updatedOrder);
    }

    @Transactional
    public OrderResponse updateOrder(int orderId, OrderRequest request) {
        Order order = orderRepository.findByIdAndIsDeleteFalse(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setTotalAmount(request.getTotalAmount());

        Order updatedOrder = orderRepository.save(order);
        return convertToOrderResponse(updatedOrder);
    }

    @Transactional
    public void deleteOrder(int orderId) {
        Order order = orderRepository.findByIdAndIsDeleteFalse(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setDelete(true);
        orderRepository.save(order);
    }

    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = modelMapper.map(order, OrderResponse.class);

        if (order.getUser() != null) {
            response.setFullName(order.getUser().getFullName());
        }

        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProductId();

            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setId(orderItem.getId());
            itemResponse.setProductId(product.getId());
            itemResponse.setProductName(product.getName());
            itemResponse.setQuantity(orderItem.getQuantity());
            itemResponse.setUnitPrice(product.getPrice()); // Sử dụng giá từ Product
            itemResponse.setTotalPrice(product.getPrice() * orderItem.getQuantity());
            itemResponse.setImage(product.getImage());

            orderItemResponses.add(itemResponse);
        }

        response.setOrderItems(orderItemResponses);
        return response;
    }
}
