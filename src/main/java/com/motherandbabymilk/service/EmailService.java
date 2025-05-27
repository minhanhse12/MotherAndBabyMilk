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

    public EmailService() {
    }

    public void sendEmail(EmailDetail emailDetail, String emailType) {
        try {
            Context context = new Context();
            String template = "";
            if ("registration".equals(emailType)) {
                context.setVariable("title", "Welcome!" + emailDetail.getReceiver().getUsername());
                context.setVariable("mainMessage", "Thank you for joining us. We're excited to have you on board!");
                context.setVariable("actionUrl", "https://9012-118-69-70-166.ngrok-free.app");
                context.setVariable("actionText", "Get Started");
                template = this.templateEngine.process("email-template", context);
            } else {
                if (!"orderCancellation".equals(emailType)) {
                    throw new MessagingException("Invalid Email Type");
                }

                context.setVariable("title", "Order Cancellation Notification");
                context.setVariable("customerName", emailDetail.getReceiver().getUsername());
                context.setVariable("mainMessage", "Your order has been cancelled. ");
                context.setVariable("reason", emailDetail.getReason());
                context.setVariable("actionUrl", "https://9012-118-69-70-166.ngrok-free.app");
                context.setVariable("actionText", "View Orders");
                context.setVariable("companyName", "KoiDelivery");
                context.setVariable("companyAddress", "566 Vo Van Ngan Street, HCM City, VietNam");
                template = this.templateEngine.process("cancel-template", context);
            }

            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom("koideliveryordering@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getReceiver().getUsername());
            mimeMessageHelper.setText(template, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            this.javaMailSender.send(mimeMessage);
        } catch (MessagingException var7) {
            System.out.println("ERROR SENT MAIL!!");
        }

    }
}