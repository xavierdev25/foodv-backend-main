package com.foodv.backend.domain.port.out.notification;

public interface PushNotificationPort {
    void sendToUser(String userId, String title, String body);
    void sendToTopic(String topic, String title, String body);
    boolean isAvailable();
}