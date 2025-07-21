package com.motherandbabymilk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.motherandbabymilk.dto.NotificationPayload;
import com.motherandbabymilk.entity.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(NotificationWebSocketHandler.class);
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("sub");
        if (username == null) {
            username = session.getId(); // Sử dụng session ID làm fallback
        }
        sessions.put(username, session);
        logger.info("Connected username: {}", username);

        // Gửi thông báo "You are online"
        sendMessageToUser(username, new NotificationPayload(username, "You are online", Notification.Source.USER, 0L));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("sub");
        if (username == null) {
            username = session.getId();
        }
        sessions.remove(username);
        logger.info("Disconnected username: {}", username);
    }

    public void sendNotification(NotificationPayload payload) throws IOException {
        if (payload.getSource() == Notification.Source.ADMIN) {
            // Gửi tới người dùng cụ thể
            WebSocketSession session = sessions.get(payload.getUsername());
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
            }
        } else if (payload.getSource() == Notification.Source.USER) {
            // Gửi tới tất cả admin
            for (WebSocketSession session : sessions.values()) {
                String role = (String) session.getAttributes().get("authorities");
                if ("ADMIN".equals(role) && session.isOpen()) {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
                }
            }
        }
    }

    private void sendMessageToUser(String username, NotificationPayload payload) throws IOException {
        WebSocketSession session = sessions.get(username);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
        }
    }
}