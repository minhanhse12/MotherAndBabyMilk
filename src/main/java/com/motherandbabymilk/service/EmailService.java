package com.motherandbabymilk.service;

import com.motherandbabymilk.dto.EmailDetail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void sendEmail(EmailDetail emailDetail, String emailType) {
        try {
            Context context = new Context();
            String template = "";

            switch (emailType) {
                case "registration":
                    context.setVariable("title", "Welcome! " + emailDetail.getReceiver().getUsername());
                    context.setVariable("mainMessage", "Thank you for joining us. We're excited to have you on board!");
                    context.setVariable("actionUrl", "http://localhost:8080/");
                    context.setVariable("actionText", "Get Started");
                    template = this.templateEngine.process("email-template", context);
                    break;

                case "orderCancellation":
                    context.setVariable("title", "Order Cancellation Notification");
                    context.setVariable("customerName", emailDetail.getReceiver().getUsername());
                    context.setVariable("mainMessage", "Your order has been cancelled.");
                    context.setVariable("reason", emailDetail.getReason());
                    context.setVariable("actionUrl", "https://644f-118-69-182-149.ngrok-free.app");
                    context.setVariable("actionText", "View Orders");
                    context.setVariable("companyName", "MnBMilk");
                    context.setVariable("companyAddress", "566 Vo Van Ngan Street, HCM City, VietNam");
                    template = this.templateEngine.process("cancel-template", context);
                    break;

                case "resetPassword":
                    context.setVariable("title", "Password Reset Request");
                    context.setVariable("mainMessage", "We received a request to reset your password.");
                    context.setVariable("actionUrl", "https://644f-118-69-182-149.ngrok-free.app/reset-password?token=" + emailDetail.getToken());
                    context.setVariable("actionText", "Reset Password");
                    context.setVariable("companyName", "MnBMilk");
                    template = this.templateEngine.process("reset-password-template", context);
                    break;

                default:
                    throw new MessagingException("Invalid Email Type");
            }

            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom("minhanh0381672@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getReceiver().getUsername());
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            this.javaMailSender.send(mimeMessage);
        } catch (MessagingException var7) {
            System.out.println("ERROR SENT MAIL!!");
        }
    }

}