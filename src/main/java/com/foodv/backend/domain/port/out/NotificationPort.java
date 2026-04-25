package com.foodv.backend.domain.port.out;

import com.foodv.backend.domain.model.notification.NotificationEvent;

public interface NotificationPort {

    void notifyUser(Long userId, NotificationEvent event);

    void notifyStore(Long storeId, NotificationEvent event);

    void notifyOrderUpdate(Long orderId, NotificationEvent event);
}
