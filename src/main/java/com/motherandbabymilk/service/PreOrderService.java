package com.motherandbabymilk.service;

import com.motherandbabymilk.dto.EmailDetail;
import com.motherandbabymilk.dto.request.PreOrderRequest;
import com.motherandbabymilk.dto.response.PreOrderResponse;
import com.motherandbabymilk.entity.PreOrder;
import com.motherandbabymilk.entity.PreOrderStatus;
import com.motherandbabymilk.entity.Product;
import com.motherandbabymilk.entity.Users;
import com.motherandbabymilk.exception.EntityNotFoundException;
import com.motherandbabymilk.repository.PreOrderRepository;
import com.motherandbabymilk.repository.ProductRepository;
import com.motherandbabymilk.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PreOrderService {

    @Autowired
    private PreOrderRepository preOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${vnpay.returnUrl:http://localhost:5173/result}")
    private String paymentUrl;

    @Transactional
    public PreOrderResponse createPreOrder(int userId, PreOrderRequest request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + request.getProductId() + " not found"));

        if (product.getQuantity() > 0) {
            throw new IllegalStateException("Product is in stock. Use cart instead.");
        }

        boolean alreadyExists = preOrderRepository.existsByUserIdAndProductIdAndStatus(userId, request.getProductId(), PreOrderStatus.PENDING);
        if (alreadyExists) {
            throw new IllegalStateException("You have already pre-ordered this product.");
        }

        LocalDateTime now = LocalDateTime.now();
        PreOrder preOrder = new PreOrder();
        preOrder.setUser(user);
        preOrder.setProduct(product);
        preOrder.setQuantity(request.getQuantity());
        preOrder.setNote(request.getNote());
        preOrder.setStatus(PreOrderStatus.PENDING);
        preOrder.setCreatedAt(now);

        PreOrder savedPreOrder = preOrderRepository.save(preOrder);
        PreOrderResponse response = modelMapper.map(savedPreOrder, PreOrderResponse.class);
        response.setProductName(product.getName());
        return response;
    }

    @Transactional
    public PreOrderResponse updatePreOrderStatus(int preOrderId, PreOrderStatus status) {
        PreOrder preOrder = preOrderRepository.findById(preOrderId)
                .orElseThrow(() -> new EntityNotFoundException("Pre-order with ID " + preOrderId + " not found"));

        LocalDateTime now = LocalDateTime.now();
        preOrder.setStatus(status);

        if (status == PreOrderStatus.CONFIRMED) {
            preOrder.setConfirmedAt(now);
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setReceiver(preOrder.getUser());
            emailDetail.setSubject("Pre-Order Confirmation");
            emailDetail.setLink(paymentUrl + "?preOrderId=" + preOrderId);
            emailService.sendEmail(emailDetail, "preOrderConfirmation");

        } else if (status == PreOrderStatus.FULFILLED) {
            preOrder.setFulfilledAt(now);
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setReceiver(preOrder.getUser());
            emailDetail.setSubject("Pre-Order Fulfilled");
            emailDetail.setLink(paymentUrl);
            emailService.sendEmail(emailDetail, "preOrderFulfilled");
        }

        PreOrder updatedPreOrder = preOrderRepository.save(preOrder);
        PreOrderResponse response = modelMapper.map(updatedPreOrder, PreOrderResponse.class);
        response.setProductName(updatedPreOrder.getProduct().getName());
        return response;
    }

    public List<PreOrderResponse> getUserPreOrders(int userId) {
        return preOrderRepository.findByUserIdAndStatusNot(userId, PreOrderStatus.CANCELED)
                .stream()
                .map(preOrder -> {
                    PreOrderResponse response = modelMapper.map(preOrder, PreOrderResponse.class);
                    response.setProductName(preOrder.getProduct().getName());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public List<PreOrderResponse> getAllPreOrdersByStatus(PreOrderStatus status) {
        return preOrderRepository.findByStatus(status)
                .stream()
                .map(preOrder -> {
                    PreOrderResponse response = modelMapper.map(preOrder, PreOrderResponse.class);
                    response.setProductName(preOrder.getProduct().getName());
                    return response;
                })
                .collect(Collectors.toList());
    }
}