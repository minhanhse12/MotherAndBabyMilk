package com.motherandbabymilk.service;

import com.motherandbabymilk.dto.EmailDetail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
    @Autowired
    TemplateEngine templateEngine;
    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendEmail(EmailDetail emailDetail, String emailType) {
        try {
            Context context = new Context();
            String template;

            switch (emailType) {
                case "registration":
                    context.setVariable("title", "Welcome! " + emailDetail.getReceiver().getUsername());
                    context.setVariable("mainMessage", "Thank you for joining us. We're excited to have you on board!");
                    context.setVariable("actionUrl", emailDetail.getLink());
                    context.setVariable("actionText", "Get Started");
                    template = templateEngine.process("email-template", context);
                    break;

                case "orderCancellation":
                    context.setVariable("title", "Order Cancellation Notification");
                    context.setVariable("customerName", emailDetail.getReceiver().getUsername());
                    context.setVariable("mainMessage", "Your order has been cancelled.");
                    context.setVariable("reason", emailDetail.getReason());
                    context.setVariable("actionUrl", emailDetail.getLink());
                    context.setVariable("actionText", "View Orders");
                    context.setVariable("companyName", "MnBMilk");
                    context.setVariable("companyAddress", "566 Vo Van Ngan Street, HCM City, VietNam");
                    template = templateEngine.process("cancel-template", context);
                    break;

                case "resetPassword":
                    context.setVariable("title", "Password Reset Request");
                    context.setVariable("mainMessage", "We received a request to reset your password.");
                    context.setVariable("actionUrl", emailDetail.getLink());
                    context.setVariable("actionText", "Reset Password");
                    context.setVariable("companyName", "MnBMilk");
                    template = templateEngine.process("reset-password-template", context);
                    break;

                case "preOrderConfirmation":
                    context.setVariable("title", "Pre-Order Confirmation");
                    context.setVariable("mainMessage", "Your pre-order has been confirmed. Please proceed with payment.");
                    context.setVariable("actionUrl", emailDetail.getLink());
                    context.setVariable("actionText", "Proceed to Payment");
                    context.setVariable("companyName", "MnBMilk");
                    template = templateEngine.process("preorder-confirmation-template", context);
                    break;

                case "preOrderFulfilled":
                    context.setVariable("title", "Pre-Order Fulfilled");
                    context.setVariable("mainMessage", "Your pre-order has been fulfilled. Thank you for your purchase!");
                    context.setVariable("actionUrl", emailDetail.getLink());
                    context.setVariable("actionText", "View Pre-Orders");
                    context.setVariable("companyName", "MnBMilk");
                    template = templateEngine.process("preorder-fulfilled-template", context);
                    break;

                case "preOrderConfirmation":
                    context.setVariable("title", "Pre-Order Confirmation");
                    context.setVariable("mainMessage", "Your pre-order has been confirmed. Please proceed with payment.");
                    context.setVariable("actionUrl", emailDetail.getLink());
                    context.setVariable("actionText", "Proceed to Payment");
                    context.setVariable("companyName", "MnBMilk");
                    template = templateEngine.process("preorder-confirmation-template", context);
                    break;

                case "preOrderCanceled":
                    context.setVariable("title", "Pre-Order Canceled");
                    context.setVariable("mainMessage", "Your pre-order has been canceled!");
                    context.setVariable("actionUrl", emailDetail.getLink());
                    context.setVariable("actionText", "View Pre-Orders");
                    context.setVariable("companyName", "MnBMilk");
                    template = templateEngine.process("preorder-canceled-template", context);
                    break;

                default:
                    throw new MessagingException("Invalid Email Type");
            }

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(senderEmail);
            mimeMessageHelper.setTo(emailDetail.getReceiver().getUsername());
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}