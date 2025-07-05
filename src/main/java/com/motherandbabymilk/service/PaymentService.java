package com.motherandbabymilk.service;

import com.motherandbabymilk.entity.*;
import com.motherandbabymilk.exception.EntityNotFoundException;
import com.motherandbabymilk.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    private static final String VNPAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String VNP_HASH_SECRET = "VI6CNUGHU58HI2U74JXGYSR8MTWB90LQ";
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    public String generatePayment(int orderId, HttpServletRequest request) {
        try {

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Order not found"));
            double totalAmount = order.getTotalAmount();
            String ipAddress= getClientIp(request);
            Map<String, String> vnpParams = new TreeMap<>();
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", "PXKV3DFK");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", String.valueOf(orderId));
            vnpParams.put("vnp_OrderInfo", "Payment for order " + orderId);
            vnpParams.put("vnp_OrderType", "order");
            vnpParams.put("vnp_Amount", String.valueOf((int) (totalAmount * 100))); // Convert to VND in cents
            vnpParams.put("vnp_ReturnUrl", "http://localhost:5173/successpayment");
            vnpParams.put("vnp_IpAddr", ipAddress);
            vnpParams.put("vnp_CreateDate", getCurrentDate());

            StringBuilder signDataBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8).toString());
                signDataBuilder.append("=");
                signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8).toString());
                signDataBuilder.append("&");
            }
            signDataBuilder.deleteCharAt(signDataBuilder.length() - 1);

            String signData = signDataBuilder.toString();
            String signed = generateHMAC(VNP_HASH_SECRET, signData);

            vnpParams.put("vnp_SecureHash", signed);

            StringBuilder urlBuilder = new StringBuilder(VNPAY_URL);
            urlBuilder.append("?");
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8).toString());
                urlBuilder.append("=");
                urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8).toString());
                urlBuilder.append("&");
            }

            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
            return urlBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getCurrentDate() {
        return java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .format(java.time.LocalDateTime.now());
    }

    public String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return "14.186.90.254";
    }


    private String generateHMAC(String secretKey, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
        SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        sha512_HMAC.init(secret_key);

        byte[] hash = sha512_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public String handleVnpayCallback(String url) {
        try {
            Map<String, String> vnpParams = UriComponentsBuilder.fromUriString(url)
                    .build()
                    .getQueryParams()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().get(0)
                    ));

            String transactionStatus = vnpParams.get("vnp_TransactionStatus");
            String transactionID = vnpParams.get("vnp_TransactionNo");
            String orderID = vnpParams.get("vnp_TxnRef");
            double amount = Double.parseDouble(vnpParams.get("vnp_Amount")) / 100.0;

            int orderId = Integer.parseInt(orderID);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Order not found"));

            if ("00".equals(transactionStatus)) {
                // PAYMENT THÀNH CÔNG
                createPayment(transactionID, orderId, amount);

                // Trừ số lượng sản phẩm từ inventory
                for (OrderItem item : order.getOrderItems()) {
                    Product product = item.getProductId();
                    if (product.getQuantity() < item.getQuantity()) {
                        throw new IllegalStateException("Insufficient stock for product ID: " + product.getId());
                    }
                    product.setQuantity(product.getQuantity() - item.getQuantity());
                    productRepository.save(product);
                }

                // Cập nhật order status
                updateOrderStatus(orderId, OrderStatus.PAID);
                orderRepository.save(order);

                // Xử lý cart sau khi payment thành công
                handleSuccessfulPayment(order.getUser().getId());
                return "SUCCESS";
            } else {;
                handleFailedPayment(order.getUser().getId());
                updateOrderStatus(orderId, OrderStatus.CANCELED);
                return "FAILED";
            }
        } catch (Exception e) {
            logger.error("Error processing VNPay callback", e);
            return "ERROR";
        }
    }

    public void handleSuccessfulPayment(int userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        if (cart != null && "pending_order".equals(cart.getStatus())) {
            // Xóa mềm các items đã được order (những items được select)
            List<CartItem> orderedItems = cartItemRepository.findByCartIdAndIsDeleteFalse(cart.getId())
                    .stream()
                    .filter(CartItem::isSelect)
                    .toList();

            orderedItems.forEach(item -> {
                item.setDelete(true);
                item.setAddedAt(LocalDateTime.now());
                cartItemRepository.save(item);
            });

            // Bỏ select tất cả items còn lại (nếu có)
            List<CartItem> remainingItems = cartItemRepository.findByCartIdAndIsDeleteFalse(cart.getId());
            remainingItems.forEach(item -> {
                item.setSelect(false);
                cartItemRepository.save(item);
            });

            // Cập nhật cart
            cart.setStatus("active");
            cart.setLastOrderId(null);
            cartRepository.save(cart);

            logger.info("Successfully processed payment for user {}, ordered items deleted from cart", userId);
        }
    }


    public void handleFailedPayment(int userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        if (cart != null && "pending_order".equals(cart.getStatus())) {
            // Khôi phục cart về trạng thái active, giữ nguyên tất cả items
            cart.setStatus("active");
            cart.setLastOrderId(null);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);

            logger.info("Payment failed for user {}, cart restored to active status", userId);
        }
    }

    public void createPayment(String transactionCode, int orderId, double amount) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setPaymentMethod("VNPAY");
        payment.setTransactionCode(transactionCode);
        payment.setStatus("PAID");
        paymentRepository.save(payment);
    }

private void updateOrderStatus(int orderId, OrderStatus newStatus) {
    Order order = orderRepository.findById(orderId).orElse(null);
    if (order != null) {
        if (newStatus == OrderStatus.PAID) {
            order.setPaidAt(LocalDateTime.now());
        }
        order.setStatus(newStatus);
        orderRepository.save(order);
    } else {
        System.out.println("Order not found with ID: " + orderId);
    }
}
}