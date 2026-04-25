package com.foodv.backend.domain.model.notification;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationEvent {

    private String type;
    private Long orderId;
    private Long userId;
    private Long storeId;
    private String message;
    private Object payload;
    private LocalDateTime timestamp;
}
